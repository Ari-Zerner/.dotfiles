alias ls="ls -AFG"
alias mv="mv -i"
alias cp="cp -i"
alias ln="ln -i"
alias btc="telnet ticker.bitcointicker.co 10080"
alias btctrade="python ~/.dotfiles/scripts/BTCTrade.py"
alias confidence="python ~/.dotfiles/scripts/Confidence.py"
alias matrix="cmatrix"
alias master="git checkout master"
alias dev="git checkout development; git merge master"
alias gcc="gcc -std=c99 -Wall -Wextra -pedantic"

# see https://github.com/nvbn/thefuck
eval "$(thefuck --alias)"
