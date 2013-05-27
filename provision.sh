#!/usr/bin/bash

# assume that instance is running and was started with "vagrant up"

date

#opt="-S /fvol15/projtemp/tracdell/ssh2"
opt='-o StrictHostKeyChecking=no -i /cygdrive/c/users/michael/.vagrant.d/insecure_private_key'

portopt_scp="-P 2223";

portopt_ssh="-p 2223";

#scpcmd=/fvol15/projtemp/tracdell/scp
scpcmd=scp

#sshcmd=/fvol15/projtemp/tracdell/ssh2
sshcmd=ssh

#target=sas@128.230.156.33
#target=sas@ec2-184-73-115-56.compute-1.amazonaws.com
target=vagrant@localhost

# --------------- provisioning ---------------------------
echo "SCRIPT: Tarring up puppet repo ..."
rm -f puppet.tar
tar cf puppet.tar puppet

echo "SCRIPT: Uploading puppet repo ..."
$scpcmd $portopt_scp $opt puppet.tar $target:/tmp

echo "SCRIPT: On Instance: Updating OS ..."
$sshcmd $portopt_ssh $opt $target "sudo apt-get update"

echo "SCRIPT: On Instance: Unzipping puppet repo ..."
$sshcmd $portopt_ssh $opt $target "cd /etc; sudo tar xf /tmp/puppet.tar"
 
# provision the instance
echo "SCRIPT: On Instance: Provisioning with puppet ..."
$sshcmd $portopt_ssh $opt $target "cd /etc/puppet; sudo puppet apply --verbose manifests/site.pp "

date
