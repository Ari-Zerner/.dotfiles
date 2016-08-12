# Usage: clearhistory
# Clears bash history
function clearhistory() {
    clear
    cat /dev/null > ~/.bash_history
    history -cw
}

# Usage: clean
# Clears Downloads, Trash, and bash history
function clean() {
    rm -rf ~/Downloads/ ~/.Trash/
    clearhistory
}

# Usage: sclean
# Securely clears Downloads, Trash, and bash history
function sclean() {
    rm -fP .bash_history
    rm -rfP ~/Downloads/ ~/.Trash/
    clearhistory
}

# Usage: csssh [host]
# SSHs into a CS@RIT computer
# host defaults to siren
function csssh() {
    if [ $# == 0 ]; then
        ssh -Y afz8559@siren.cs.rit.edu
    elif [ $# == 1 ]; then
        ssh -Y afz8559@$1.cs.rit.edu
    fi
}

# Usage: csftp [host]
# FTPs to a CS@RIT computer
# host defaults to siren
function csftp() {
    if [ $# == 0 ]; then
        sftp afz8559@siren.cs.rit.edu
    elif [ $# == 1 ]; then
        sftp afz8559@$1.cs.rit.edu
    fi
}

# Usage: ssh-copy-id hostname
# Copies SSH public key from local computer to a remote host
function ssh-copy-id() {
    cat ~/.ssh/id_rsa.pub | ssh $1 "mkdir -p ~/.ssh && cat >>  ~/.ssh/authorized_keys"
}

# Usage: hash algorithm file [expected]
# Usage: hash -m algorithm message [expected]
# Hashes a message or file using the given algorithm
# If expected is provided, compares the hash to expected
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

# Usage: checkmerge src dest
# Shortcut for:
#   git checkout dest
#   git merge src
function checkmerge() {
  if [ $# -eq 2 ]
  then
    git checkout $2
    git merge $1
  else
    echo "Usage: checkmerge src dest"
  fi
}

# Usage: findtext text [dir]
# Recursively finds text in files in the provided or current directory
function findtext() {
  if [ $# -eq 0 -o $# -gt 2 ]
  then
    echo "Usage: findtext text [dir]"
    return
  fi
  TEXT="$1"
  if [ $# -eq 2 ]
  then
    DIR="$2"
  else
    DIR="."
  fi
  grep -rnl "$DIR" -e "$TEXT"
}
