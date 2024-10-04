#!/usr/bin/env sh

# Detect the current shell
if [ -n "$ZSH_VERSION" ]; then
    CURRENT_SHELL="zsh"
elif [ -n "$BASH_VERSION" ]; then
    CURRENT_SHELL="bash"
else
    CURRENT_SHELL="sh"
fi

if [[ $COLORTERM = gnome-* && $TERM = xterm ]] && infocmp gnome-256color >/dev/null 2>&1; then
  export TERM=gnome-256color
elif infocmp xterm-256color >/dev/null 2>&1; then
  export TERM=xterm-256color
fi

if tput setaf 1 &> /dev/null; then
  tput sgr0
  if [[ $(tput colors) -ge 256 ]] 2>/dev/null; then
    MAGENTA=$(tput setaf 9)
    ORANGE=$(tput setaf 172)
    GREEN=$(tput setaf 190)
    PURPLE=$(tput setaf 141)
    WHITE=$(tput setaf 256)
    CYAN=$(tput setaf 45)
  else
    MAGENTA=$(tput setaf 5)
    ORANGE=$(tput setaf 4)
    GREEN=$(tput setaf 2)
    PURPLE=$(tput setaf 1)
    WHITE=$(tput setaf 7)
    CYAN=$(tput setaf 6)
  fi
  BOLD=$(tput bold)
  RESET=$(tput sgr0)
else
  MAGENTA="\033[1;31m"
  ORANGE="\033[1;33m"
  GREEN="\033[1;32m"
  PURPLE="\033[1;35m"
  WHITE="\033[1;37m"
  CYAN="\033[1;36m"
  BOLD=""
  RESET="\033[m"
fi

# Fastest possible way to check if repo is dirty. a savior for the WebKit repo.
function parse_git_dirty() {
  git diff --quiet --ignore-submodules HEAD 2>/dev/null; [ $? -eq 1 ] && echo '*'
}

function parse_git_branch() {
  git branch --no-color 2> /dev/null | sed -e '/^[^*]/d' -e "s/* \(.*\)/\1$(parse_git_dirty)/"
}

# Function to get SSH information
function get_ssh_info() {
  if [ -n "$SSH_CLIENT" ] || [ -n "$SSH_TTY" ]; then
    echo "${USER}@${HOSTNAME}"
  fi
}

# Shell-specific prompt settings
if [ "$CURRENT_SHELL" = "zsh" ]; then
    # ZSH-specific prompt
    setopt PROMPT_SUBST
    PROMPT='$(date "+%b %d %H:%M") %{$CYAN%}$(get_ssh_info)%{$WHITE%}  %{$BOLD%}%{$GREEN%}%~%{$WHITE%}$([[ -n $(git branch 2> /dev/null) ]] && echo " on ")%{$PURPLE%}$(parse_git_branch)%{$WHITE%}
$ %{$RESET%}'
elif [ "$CURRENT_SHELL" = "bash" ]; then
    # Bash-specific prompt
    PS1="\$(date \"+%b %d %H:%M\") \[$CYAN\]\$(get_ssh_info)\[$WHITE\]  \[\e]2;$PWD\[\a\]\[\e]1;\]$(basename "$(dirname "$PWD")")/\W\[\a\]${BOLD}\[$GREEN\]\w\[$WHITE\]\$([[ -n \$(git branch 2> /dev/null) ]] && echo \" on \")\[$PURPLE\]\$(parse_git_branch)\[$WHITE\]\n\$ \[$RESET\]"
fi

