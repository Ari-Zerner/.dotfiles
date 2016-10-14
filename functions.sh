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
function csssh() {
    if [ $# == 0 ]; then
        HOST="$CSRITHOST"
    else
        HOST="$1"
    fi
    ssh -Y "afz8559@$HOST.cs.rit.edu"
}

# Usage: csftp [host]
# FTPs to a CS@RIT computer
function csftp() {
    if [ $# == 0 ]; then
        HOST="$CSRITHOST"
    else
        HOST="$1"
    fi
    sftp "afz8559@$HOST.cs.rit.edu"
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

# Usage: cpvim oldFile newFile
# Copies oldFile to newFile, then opens vim to edit newFile
function cpvim() {
    cp "$1" "$2"
    vim "$2"
}

# Usage: c source1 [source2] [-flag1] ...
# Sources are the names of existing .c files but have no file extension
# Flags are compiler flags passed on to gcc
# Compiles the sources and links them into a program with the same name as source1, then runs the program
# Deletes any previous version of the program and all object files created
function c() {
    if [[ $1 == *.c ]]
    then
        echo "Error: $0 takes a program name, not a source file."
        return
    fi
    if [ -f "./$1" ]; then rm "./$1"; fi
    GCC_C="gcc -c"
    GCC_O="gcc -o $1"
    RM="rm"
    for ARG in $@
    do
        if [[ $ARG == -* ]]
        then
            GCC_C="$GCC_C $ARG"
            GCC_O="$GCC_O $ARG"
        else
            GCC_C="$GCC_C $ARG.c"
            GCC_O="$GCC_O $ARG.o"
            RM="$RM $ARG.o"
        fi
    done
    eval "$GCC_C; $GCC_O; $RM"
    if [ -f "./$1" ]; then "./$1"; fi
}

# Usage: mkcd dir
# mkdir dir && cd dir
function mkcd() {
    mkdir -p "$1"
    cd "$1"
}

# Usage: revisions
# Prints the Git revisions
# Usage: revisions [revisions_file]
# Writes the Git revisions to revisions_file
function revisions() {
    if [ $# == 0 ]
        then git log --stat --pretty=short --graph
    elif [ $# == 1 ]
        then revisions > $1
    else
        echo "Usage: $0 [revisions_file]"
    fi
}

# Usage: pushchanges
# Git: commits on current branch, merges to master, and pushes master
function pushchanges() {
    git commit -a
    git checkout master
    git merge -
    git push
}
