import urllib, json, sys

# constants
ask_flat_fee = 8
ask_value_fee = 1.02
bid_flat_fee = 8
bid_value_fee = 1.02
exchange = 'Coinbase'

# fetch JSON data from a URL
def get_json(url):
    return json.loads(urllib.urlopen(url).read())

# get the BTC price from an exchange
def get_price():
    print 'Retrieving price from %s...' % exchange
    try:
        price = None
        if exchange == 'Coinbase':
            json = get_json('https://api.coinbase.com/v2/prices/spot')
            price = json['data']['amount']
        elif exchange == 'Gemini':
            json = get_json('https://api.gemini.com/v1/pubticker/BTCUSD')
            price = json['ask']
        else:
            raise Exception
        print 'Retrieved price from %s.' % exchange
        return float(price)
    except Exception:
        print 'Could not retrieve price from %s.' % exchange

# format a dollar amount
def dollar_string(amount):
    return '$%.10g' % round(amount, 2)

# format a Bitcoin amount
def btc_string(amount):
    amount = round(amount, 8)
    return '%.10g BTC (%.10g bits)' % (amount, 1000000 * amount)

def main(argv):
    price = get_price()
    if price == None:
        return
    if len(argv) == 1:
        argv.append('B1')
    if len(argv) == 2:
        amount = argv[1]
        dollars = True
        if amount.startswith('B'):
            dollars = False
            amount = amount[1:]
        try:
            amount = float(amount)
            if dollars:
                print 'Ask: %s for %s' % (dollar_string(amount),\
                btc_string((amount - ask_flat_fee) / price / ask_value_fee))
                print 'Raw: %s = %s' % (dollar_string(amount),\
                btc_string(amount / price))
                print 'Bid: %s for %s' % (dollar_string(amount),\
                btc_string((amount + bid_flat_fee) / price * bid_value_fee))
            else:
                print 'Ask: %s for %s' %\
                (dollar_string(amount * price * ask_value_fee + ask_flat_fee),\
                btc_string(amount))
                print 'Raw: %s = %s' % (dollar_string(amount * price),\
                btc_string(amount))
                print 'Bid: %s for %s' %\
                (dollar_string(amount * price / bid_value_fee - bid_flat_fee),\
                btc_string(amount))
        except ValueError:
            print 'Amount must be a number, optionally preceeded by a B.'
    else:
        print 'usage: btctrade [amount]'

main(sys.argv)
