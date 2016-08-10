import urllib, json, sys

# get the BTC price from Coinbase
def get_price():
    url = "https://api.coinbase.com/v2/prices/spot"
    response = urllib.urlopen(url)
    data = json.loads(response.read())["data"]
    return float(data["amount"])

# format a dollar amount
def dollar_string(amount):
    return '$%.10g' % round(amount, 2)

# format a Bitcoin amount
def btc_string(amount):
    amount = round(amount, 8)
    return '%.10g BTC (%.10g bits)' % (amount, 1000000 * amount)

def main(argv):
    price = get_price()
    ask = price * 1.04
    bid = price / 1.00
    if len(argv) == 1:
        print 'Ask: %s for %s' % (dollar_string(ask), btc_string(1))
        print 'Bid: %s for %s' % (dollar_string(bid), btc_string(1))
    elif len(argv) == 2:
        amount = argv[1]
        dollars = True
        if amount.startswith('B'):
            dollars = False
            amount = amount[1:]
        try:
            amount = float(amount)
            if dollars:
                print 'Ask: %s for %s' % (dollar_string(amount),
                        btc_string(amount / ask))
                print 'Bid: %s for %s' % (dollar_string(amount),
                        btc_string(amount / bid))
            else:
                print 'Ask: %s for %s' % (dollar_string(amount * ask),
                        btc_string(amount))
                print 'Bid: %s for %s' % (dollar_string(amount * bid),
                        btc_string(amount))
        except ValueError:
            print 'Amount must be a number, optionally preceeded by a B.'
    else:
        print 'usage: btctrade [amount]'

main(sys.argv)
