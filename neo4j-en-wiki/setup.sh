#!/bin/sh
set -e

echo "Remove existing installation if exists"
docker compose down --volumes
docker compose rm --volumes --stop --force

echo "Start and Stop Neo4j to initialize the volumes"
docker compose up --detach --wait
docker compose down

echo "Import the CSV files"
script_dir=$(dirname "$(which "$0" 2>/dev/null || realpath ./"$0")")
header_dir="$script_dir/../neo4j-import-headers"
data_dir="${HOME}/wikidata/en_wiki"
report="$script_dir/import.report"
echo "" >"${report}"
docker run --interactive --tty --rm \
  --volume=neo4j-en-wiki_data:/data \
  --volume="${data_dir}":/import \
  --volume="${header_dir}":/import-headers \
  --volume="${report}":/var/lib/neo4j/import.report \
  neo4j:5.5 \
  neo4j-admin database import full --overwrite-destination \
  --nodes=Page=/import-headers/page_headers.csv,/import/pages-\\d+.csv \
  --relationships=LINKS_TO=/import-headers/link_headers.csv,/import/links-\\d+.csv \
  --skip-bad-relationships
