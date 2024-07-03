# -*- encoding: utf-8 -*-
"""
CARDANO BALLOT KERI VERIFIER
cli.commands module
"""
import traceback

import multicommand
from keri import help

from verifier.cli import commands

logger = help.ogler.getLogger()


def main():
    parser = multicommand.create_parser(commands)
    args = parser.parse_args()

    if not hasattr(args, 'handler'):
        parser.print_help()
        return

    try:
        args.handler(args)

    except Exception as ex:
        print(f"ERR: {ex}")
        traceback.print_exception(ex)
        return -1


if __name__ == "__main__":
    main()
