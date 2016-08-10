function clearhistory() {
    clear
    cat /dev/null > ~/.bash_history
    history -cw
}

function clean() {
    rm -rf ~/Downloads/ ~/.Trash/
    clearhistory
}

function sclean() {
    rm -fP .bash_history
    rm -rfP ~/Downloads/ ~/.Trash/
    clearhistory
}

function rpn() {
    pushd ~/Google\ Drive/Java\ Programs/RPN/
    java -jar dist/RPN.jar
    popd
}

function csssh() {
    if [ $# == 0 ]; then
        ssh -Y afz8559@siren.cs.rit.edu
    elif [ $# == 1 ]; then
        ssh -Y afz8559@$1.cs.rit.edu
    fi
}

function csftp() {
    if [ $# == 0 ]; then
        sftp afz8559@siren.cs.rit.edu
    elif [ $# == 1 ]; then
        sftp afz8559@$1.cs.rit.edu
    fi
}

function ssh-copy-id() {
    cat ~/.ssh/id_rsa.pub | ssh $1 "mkdir -p ~/.ssh && cat >>  ~/.ssh/authorized_keys"
}

function seup() {
    cdse
    svn up
    rm -r accounts/migrations
    pymanage makemigrations
    pymanage migrate
}

function hash() {
    if [[ $1 == "-m" ]]
    then
        FILE=".hash.tmp"
        DELETEFILE=true
        echo $3 > $FILE
        shift 1
    else
        FILE=$2
        if [ ! -f "$FILE" ]
        then
            echo "$FILE: No such file or directory"
            echo "Use hash -m to hash a message instead of a file"
            return
        fi
        DELETEFILE=false
    fi
    HASH=`openssl dgst -$1 "$FILE"`
    HASH="${HASH##* }"
    if [ $# -eq 2 ]
    then
        echo $HASH
    elif [ $# -eq 3 ]
    then
        if [[ $HASH == $3 ]]
        then
            echo "Checksum matches."
        else
            echo "Checksum does not match."
        fi
    else
        echo "Usage: hash algorithm file [expected]"
        echo "Usage: hash -m algorithm message [expected]"
    fi
    if [[ $DELETEFILE == true ]]
    then rm "$FILE"
    fi
}
