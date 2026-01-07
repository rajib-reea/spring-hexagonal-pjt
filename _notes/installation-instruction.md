```
wsl ubuntu bootstrap:

Here is java, postgresql, docker, git and maven installation commands-

wsl --list --running
wsl -l -v

sudo apt update && sudo apt upgrade -y
sudo apt install -y wget gpg
wget -O - https://apt.corretto.aws/corretto.key \
  | sudo gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" \
  | sudo tee /etc/apt/sources.list.d/corretto.list
sudo apt update
sudo apt install -y java-25-amazon-corretto-jdk
sudo apt install -y java-25-amazon-corretto-jdk


java --version
javac --version
readlink -f $(which java)
export JAVA_HOME=/usr/lib/jvm/java-25-corretto
export PATH=$JAVA_HOME/bin:$PATH

////////////////
[Install database]
sudo apt update
sudo apt upgrade -y
sudo apt install postgresql postgresql-contrib -y
sudo service postgresql status
sudo service postgresql start
sudo -i -u postgres
psql
select 1+1;
ALTER USER postgres PASSWORD 'postgres';
CREATE DATABASE acc_report_app;
CREATE USER acc_report_app WITH ENCRYPTED PASSWORD 'acc_report_app';
GRANT ALL PRIVILEGES ON DATABASE acc_report_app TO acc_report_app;
sudo -i -u postgres
psql
\c acc_report_app
-- Create the schema that your Spring Boot app is looking for
CREATE SCHEMA acc_report_app;

-- Make the app user the owner of this new schema
ALTER SCHEMA acc_report_app OWNER TO acc_report_app;

-- Grant permissions just in case
GRANT ALL ON SCHEMA acc_report_app TO acc_report_app;

psql -U acc_report_app -d acc_report_app -h localhost
sudo service postgresql start > /dev/null 2>&1

/////
[Install Docker client]
sudo apt update
sudo apt upgrade -y
sudo apt install -y docker.io
docker --version
export DOCKER_HOST=tcp://localhost:2375
source ~/.bashrc
docker info
sudo usermod -aG docker $USER
[logout and wsl to take the above command to take effect.]
docker run hello-world
//for compose plugin
sudo apt update
sudo apt install docker-compose-plugin -y
docker compose version
ps aux | grep dockerd



[Install Git]
git config --global user.name "Md. Rajib Hossain Pavel"
git config --global user.email "rajib.pavel@erainfotechbd.com"
git config --list
ls -al ~/.ssh

ssh-keygen -t ed25519 -C "rajib.pavel@erainfotechbd.com"
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519
cat ~/.ssh/id_ed25519.pub

Go to Setting and add SSH Key
ssh -T git@github.com
cd ~
git config --global core.filemode false
git clone 

[Install Maven]

sudo apt update
sudo apt install maven -y
mvn -version

[Install SDKMan]
sudo apt update
sudo apt install zip unzip -y
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk version
sdk list java
sdk install java 25.0.1-amzn
sdk current java
java -version
mvn -version
//the following lines shows something extraordinary- the sdkman java has taken precedence over system java(previously installed)
which java
/home/rajib/.sdkman/candidates/java/current/bin/java
sdk use java 21.0.9-amz
sdk default java 21.0.9-amzn
sdk current java
echo 'export SDKMAN_DIR="$HOME/.sdkman"' >> ~/.bashrc
echo '[[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]] && source "$SDKMAN_DIR/bin/sdkman-init.sh"' >> ~/.bashrc
source ~/.bashrc
echo $JAVA_HOME

```