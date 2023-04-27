$ErrorActionPreference="Stop"

Write-Host "Remove existing installation if exists"
docker compose down --volumes
docker compose rm --volumes --stop --force

Write-Host "Start and Stop Neo4j to initialize the volumes"
docker compose up --detach --wait
docker compose down

Write-Host "Import the CSV files"
$script_path = $MyInvocation.MyCommand.Path
if (!$script_path) {$script_path = $psISE.CurrentFile.Fullpath}
if ($script_path)  {$script_path = Split-Path $script_path -Parent}
$header_dir = "${script_path}\..\neo4j-import-headers"
$data_dir = "${Env:USERPROFILE}\wikidata\simple_wiki"
$report = "${script_path}\import.report"
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
