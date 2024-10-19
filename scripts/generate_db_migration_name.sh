#!/bin/bash

# Check if an argument is provided (i.e., the migration name)
if [ -z "$1" ]; then
  echo "Usage: $0 <migration_name>"
  exit 1
fi

# Get the current timestamp in the format YYYY.MM.DD.HH.MM.SS
TIMESTAMP=$(date +"%Y.%m.%d.%H.%M.%S")

# The migration name provided as the argument
MIGRATION_NAME=$1

# Construct the Flyway migration file name
MIGRATION_FILE="V${TIMESTAMP}__${MIGRATION_NAME}.sql"

# Output the file name
echo "$MIGRATION_FILE"
