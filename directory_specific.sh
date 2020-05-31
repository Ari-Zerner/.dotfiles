# commands to be run when a terminal is opened in a particular directory

DIR="$(dirs -0)" # when this is run, dotfiles will be on top of the dir stack
BASEDIR="$(basename "$DIR")"

case $BASEDIR in
  accounting) # accounting with hledger
    alias h="hledger"
    ;;
esac
