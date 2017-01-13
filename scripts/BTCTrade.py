import urllib, json, sys, re

# constants
exchange = 'Coinbase'

# fetch JSON data from a URL
def get_json(url):
    return json.loads(urllib.urlopen(url).read())

# get the BTC price from an exchange
def get_price():
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
    if len(argv) == 1:
        argv.append('B1')
    if len(argv) == 2:
        if re.match('^-?-?h(elp)?$', argv[1], re.IGNORECASE):
            # this will print usage message
            pass
        else:
            argv.append('0')
            argv.append('0')
    if len(argv) == 4:
        amount = argv[1]
        dollars = True
        if amount.startswith('B'):
            dollars = False
            amount = amount[1:]
        try:
            amount = float(amount)
            flat_fee = float(argv[2])
            value_fee = float(argv[3]) / 100 + 1
            price = get_price()
            if price == None:
                return
            print 'Fee is $%s plus %s percent.' % (argv[2], argv[3])
            if dollars:
                print 'Ask: %s for %s' % (dollar_string(amount),\
                btc_string((amount - flat_fee) / price / value_fee))
                print 'Bid: %s for %s' % (dollar_string(amount),\
                btc_string((amount + flat_fee) / price * value_fee))
            else:
                print 'Ask: %s for %s' %\
                (dollar_string(amount * price * value_fee + flat_fee),\
                btc_string(amount))
                print 'Bid: %s for %s' %\
                (dollar_string(amount * price / value_fee - flat_fee),\
                btc_string(amount))
        except ValueError:
            print 'Arguments must be numbers. Amount may be preceeded by a B.'
    else:
        print 'usage: btctrade [amount] [flat_fee value_fee_percent]'

main(sys.argv)
