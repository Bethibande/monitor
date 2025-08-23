#!/bin/bash

npx vite build --watch --base=/federated/web &
npx vite preview --base=/federated/web --port 5001 --strictPort


wait