# Dotfiles

These dotfiles provide a customizable shell environment for both Bash and Zsh.

## Installation

To install automatically, run the following command:
  eval "$(curl https://raw.githubusercontent.com/Ari-Zerner/.dotfiles/master/init)"

This will detect your current shell, set up the dotfiles, and load them immediately.

To install manually:
  1) Clone this repository (.dotfiles) in your home folder (~).
  2) Run the following command:
      . ~/.dotfiles/load
  3) Add the above command to your shell's config file (.bashrc for Bash, .zshrc for Zsh).

## Structure

- `init`: Installation script
- `load`: Main script that loads all dotfiles
- `*.sh`: Shell-agnostic configuration files (e.g., aliases.sh, exports.sh, functions.sh)
- `prompt.sh`: Prompt configuration for both Bash and Zsh

## Customization

To add your own customizations:
- Create or edit .sh files in the dotfiles directory
- To customize your prompt, edit prompt.sh

All .sh files will be automatically sourced when the dotfiles are loaded.
