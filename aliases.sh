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
alias ip="curl ifconfig.co"
alias gittree="git log --graph --full-history --all --color \
  --pretty=format:\"%x1b[31m%h%x09%x1b[32m%d%x1b[0m%x20%s\""

# see https://github.com/nvbn/thefuck
eval "$(thefuck --alias)"
