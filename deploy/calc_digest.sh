#!/bin/bash

DIR=$(dirname "$0")

for filename in $DIR/*; do
  if [ ! -d "$filename" ]; then
      BASE=$(basename "${filename}" | tr ' './ _)
      DIGEST="$(sha1sum "${filename}" | head -c 40)"
      export "DIGEST_$BASE=$DIGEST"
  fi
done
