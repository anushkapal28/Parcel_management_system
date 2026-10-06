#!/usr/bin/env bash
# Quick end-to-end smoke test. Run AFTER all 4 services are up. (Git Bash on Windows works too.)
set -e
A=http://localhost:8081; P=http://localhost:8082; T=http://localhost:8083
json() { sed -E 's/.*"'"$1"'":"([^"]*)".*/\1/'; }

echo "== Register admin, agent, customer"
curl -s -X POST $A/api/auth/register -H 'Content-Type: application/json' -d '{"name":"Admin","email":"admin@dp.com","password":"secret123","role":"ADMIN"}' >/dev/null || true
curl -s -X POST $A/api/auth/register -H 'Content-Type: application/json' -d '{"name":"Agent","email":"agent@dp.com","password":"secret123","role":"AGENT"}' >/dev/null || true
curl -s -X POST $A/api/auth/register -H 'Content-Type: application/json' -d '{"name":"Cust","email":"cust@dp.com","password":"secret123"}' >/dev/null || true

login() { curl -s -X POST $A/api/auth/login -H 'Content-Type: application/json' -d "{\"email\":\"$1\",\"password\":\"secret123\"}" | json token; }
ADMIN=$(login admin@dp.com); AGENT=$(login agent@dp.com); CUST=$(login cust@dp.com)

echo "== Customer creates parcel"
RES=$(curl -s -X POST $P/api/parcels -H "Authorization: Bearer $CUST" -H 'Content-Type: application/json' \
  -d '{"senderName":"Anushka","receiverName":"Rahul","receiverAddress":"Jebel Ali, Dubai","weightKg":2.5}')
echo "$RES"; TN=$(echo "$RES" | json trackingNumber)

echo "== Admin assigns to agent"
curl -s -X PUT $P/api/parcels/$TN/assign -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d '{"agentEmail":"agent@dp.com"}'; echo
echo "== Agent moves it forward"
curl -s -X PUT $P/api/parcels/$TN/status -H "Authorization: Bearer $AGENT" -H 'Content-Type: application/json' -d '{"status":"IN_TRANSIT","location":"Mumbai Hub","note":"Left hub"}'; echo
echo "== Public tracking history"
curl -s $T/api/tracking/$TN; echo
echo "== Customer tries admin action (expect 403)"
curl -s -o /dev/null -w "%{http_code}\n" -X PUT $P/api/parcels/$TN/assign -H "Authorization: Bearer $CUST" -H 'Content-Type: application/json' -d '{"agentEmail":"agent@dp.com"}'
