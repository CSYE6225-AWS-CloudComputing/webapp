#add group
sudo groupadd csye6225

#add user to group
sudo useradd -s /usr/sbin/nologin -g csye6225 csye6225

echo "========================================================================="
# shellcheck disable=SC2164
cd ../csye6225/webapp/
pwd
echo "========================================================================="
ls -al
echo "========================================================================="
sudo cp ./startup-scripts/webapp.service /etc/systemd/system

sudo chown -R csye6225:csye6225 /home/csye6225/webapp
sudo chmod 744 /home/csye6225/webapp

sudo systemctl daemon-reload
sudo systemctl enable webapp
sudo systemctl start webapp
