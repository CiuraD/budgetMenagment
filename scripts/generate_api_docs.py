#!/usr/bin/env python3
"""
Simple generator: fetch OpenAPI JSON from local services and produce markdown summaries per service.
Usage: python3 scripts/generate_api_docs.py
"""
import json
import os
import sys
from urllib.request import urlopen, Request
from urllib.error import URLError, HTTPError

# Default services (can be overridden with OPENAPI_SERVICES env var)
# Format for OPENAPI_SERVICES: name1=http://host:port/path,name2=http://...
DEFAULT_SERVICES = {
    "user-service": "http://localhost:8081/v3/api-docs",
    "budget-service": "http://localhost:8082/v3/api-docs",
}

env_val = os.environ.get("OPENAPI_SERVICES")
if env_val:
    SERVICES = {}
    for pair in env_val.split(","):
        if "=" in pair:
            name, url = pair.split("=", 1)
            SERVICES[name.strip()] = url.strip()
else:
    SERVICES = DEFAULT_SERVICES

OUT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "docs"))
TIMEOUT = int(os.environ.get("OPENAPI_TIMEOUT", "10"))


def fetch_json(url):
    req = Request(url, headers={"Accept": "application/json"})
    try:
        with urlopen(req, timeout=TIMEOUT) as r:
            return json.load(r)
    except HTTPError as e:
        print(f"HTTP error fetching {url}: {e.code} {e.reason}", file=sys.stderr)
    except URLError as e:
        print(f"URL error fetching {url}: {e}", file=sys.stderr)
    except Exception as e:
        print(f"Unexpected error fetching {url}: {e}", file=sys.stderr)
    return None


def dump_raw(name, data):
    os.makedirs(OUT_DIR, exist_ok=True)
    path = os.path.join(OUT_DIR, f"{name}-openapi.json")
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    print(f"Saved raw OpenAPI JSON to {path}")


def schema_to_text(schema):
    if not schema:
        return "(no schema)"
    if "$ref" in schema:
        return f"$ref: {schema['$ref']}"
    t = schema.get("type")
    if t == "array":
        return f"array of {schema_to_text(schema.get('items'))}"
    props = schema.get("properties")
    if props:
        out = []
        for k, v in props.items():
            out.append(f"- {k}: {v.get('type','object')}")
        return "\n".join(out)
    return str(schema)


def generate_markdown(name, openapi):
    md = []
    info = openapi.get("info", {})
    title = info.get("title", name)
    desc = info.get("description", "")
    md.append(f"# {title}\n")
    if desc:
        md.append(desc + "\n")

    servers = openapi.get("servers") or []
    if servers:
        md.append("Base servers:")
        for s in servers:
            md.append(f"- {s.get('url')}")
        md.append("")

    paths = openapi.get("paths", {})
    for path, methods in paths.items():
        for method, op in methods.items():
            summary = op.get("summary") or op.get("operationId") or ""
            md.append(f"## {method.upper()} {path}")
            if summary:
                md.append(f"- Summary: {summary}")
            if op.get("description"):
                md.append(f"- Description: {op.get('description')}")

            # Parameters
            params = op.get("parameters", [])
            if params:
                md.append("- Parameters:")
                for p in params:
                    req = p.get("required", False)
                    schema = p.get("schema")
                    md.append(f"  - {p.get('name')} (in: {p.get('in')}) required: {req} schema: {schema_to_text(schema)}")

            # Request body
            rb = op.get("requestBody")
            if rb:
                md.append("- Request body:")
                content = rb.get("content", {})
                for ctype, cval in content.items():
                    schema = cval.get("schema")
                    md.append(f"  - {ctype}:\n{schema_to_text(schema)}")

            # Responses
            responses = op.get("responses", {})
            if responses:
                md.append("- Responses:")
                for code, rval in responses.items():
                    desc = rval.get("description", "")
                    md.append(f"  - {code}: {desc}")
                    content = rval.get("content", {})
                    for ctype, cval in content.items():
                        schema = cval.get("schema")
                        md.append(f"    - {ctype}: {schema_to_text(schema)}")

            md.append("")
    return "\n".join(md)


def write_md(name, md_text):
    os.makedirs(OUT_DIR, exist_ok=True)
    path = os.path.join(OUT_DIR, f"{name}-api.md")
    with open(path, "w", encoding="utf-8") as f:
        f.write(md_text)
    print(f"Wrote markdown to {path}")


if __name__ == "__main__":
    os.makedirs(OUT_DIR, exist_ok=True)
    any_success = False
    for name, url in SERVICES.items():
        print(f"Fetching {name} OpenAPI from {url} ...")
        data = fetch_json(url)
        if not data:
            print(f"Skipping {name}, no OpenAPI JSON available at {url}")
            continue
        any_success = True
        dump_raw(name, data)
        md = generate_markdown(name, data)
        write_md(name, md)
    if not any_success:
        print("No OpenAPI docs were fetched. Ensure services are running locally and /v3/api-docs is reachable.")
        sys.exit(2)
    print("Done.")
