#!/bin/bash
#
# Bruno Duarte (bmdc@kth.se)
#
#========================================================
# Is requeried to pass at least 3 parameters
#if test $# -ge 2; then
#  echo "You need to specify at least the first 3 parameters"
#  exit 0
#fi
#========================================================
# We define the amount of containers/clients to emulate
if [[ "$1" =~ ^[0-9]+$ ]] ; then
    #Positional parameter 1 is a number
    MOCK_CLIENTS=$1
else
    #Positional parameter 1 is not a number
    echo "The amount of clients specified is not a number"
    exit 0
fi
#========================================================
# We define the GM server
GM_URL=$2
#========================================================
# We define the Data collection server
DC_URL=$3
#========================================================
# We define if we are in Midterm presentation mode
MIDTERM_DEMO=$4
#========================================================
# We spawn the amount of MOCK_CLIENTS to simulate
echo "Spawning $MOCK_CLIENTS clients"
for i in $(eval echo "{1..$MOCK_CLIENTS}")
do
  # If we want to run in detach mode, add the "-d" flag and remove the "&" from the end of the line
  docker run  -e GM_URL="$GM_URL" -e DC_URL="$DC_URL" -e MIDTERM_DEMO="$MIDTERM_DEMO" --rm --name console_client_spi_$i client_spi &
done
