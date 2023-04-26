# Neo4j simple-wiki

## Setup & Rest for Windows

```shell
Write-Host "Remove existing installation if exists"
docker compose down --volumes
docker compose rm --volumes --stop --force

Write-Host "Start and Stop Neo4j to initialize the volumes"
docker compose up --detach --wait
docker compose down

Write-Host "Import the CSV files"
$header_dir = "$(Get-Location)\..\neo4j-import-headers"
$data_dir = "${Env:USERPROFILE}\wikidata\simple_wiki"
$report = "$(Get-Location)\import.report"
Set-Content -Path $report -Value ""

docker run --interactive --tty --rm `
  --volume=neo4j-simple-wiki_data:/data `
  --volume="${data_dir}":/import `
  --volume="${header_dir}":/import-headers `
  --volume="${report}":/var/lib/neo4j/import.report `
  neo4j:5.5 `
  neo4j-admin database import full --overwrite-destination `
  --nodes=Page=/import-headers/page_headers.csv,/import/pages-\\d+.csv `
  --relationships=LINKS_TO=/import-headers/link_headers.csv,/import/links-\\d+.csv `
  --skip-bad-relationships

```

## Setup & Rest for Mac / Linux / WSL

```shell
echo "Remove existing installation if exists"
docker compose down --volumes
docker compose rm --volumes --stop --force

echo "Start and Stop Neo4j to initialize the volumes"
docker compose up --detach --wait
docker compose down

echo "Import the CSV files"
header_dir="$(pwd)/../neo4j-import-headers"
data_dir="${HOME}/wikidata/simple_wiki"
report="$(pwd)/import.report"
echo -n "" >"${report}"
docker run --interactive --tty --rm \
  --volume=neo4j-simple-wiki_data:/data \
  --volume="${data_dir}":/import \
  --volume="${header_dir}":/import-headers \
  --volume="${report}":/var/lib/neo4j/import.report \
  neo4j:5.5 \
  neo4j-admin database import full --overwrite-destination \
  --nodes=Page=/import-headers/page_headers.csv,/import/pages-\\d+.csv \
  --relationships=LINKS_TO=/import-headers/link_headers.csv,/import/links-\\d+.csv \
  --skip-bad-relationships
```

## Start

```shell
docker compose up --detach --wait
```

## Stop

```shell
docker compose down
```

