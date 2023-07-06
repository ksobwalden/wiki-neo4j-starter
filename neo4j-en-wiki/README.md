# Neo4j en-wiki

## Allow Powershell on Windows (Open Powershell in administrator mode)
```sh
Set-ExecutionPolicy unrestricted
```

## Setup & Reset for Windows

```shell
.\setup.ps1
```

## Setup & Rest for Mac / Linux / WSL

```shell
sh ./setup.sh
```

## Start

```shell
docker compose up --detach --wait
```

## Stop

```shell
docker compose down
```

