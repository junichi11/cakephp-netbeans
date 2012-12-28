#!/bin/bash
NETBEANSSUITE=""
while [ "$1" != "" ]
do
    case "$1" in
        *NetBeansSuite.php ) NETBEANSSUITE="$NETBEANSSUITE %s";;
        *run ) NETBEANSSUITE="$NETBEANSSUITE $1=$2" shift;;
        * ) NETBEANSSUITE="$NETBEANSSUITE $1";;
    esac
shift
done
%s $NETBEANSSUITE