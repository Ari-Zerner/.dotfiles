import sys
from math import log10, pow, floor, fabs, log
from fractions import Fraction

sig_digs = 4

def round_float(arg):
    f = float(arg)
    if f == 0:
        return 0
    f = round(f, sig_digs - int(floor(log10(fabs(f)))) - 1)
    if f == int(f):
        return int(f)
    return f

def nice_fraction(n, d):
    return Fraction(int(round_float(n) * pow(10, sig_digs)), int(round_float(d) * pow(10, sig_digs)))

def from_probability(p):
    if p <= 0 or p >= 1:
        raise ValueError()
    o = nice_fraction(p, 1 - p)
    l = log10(o)
    return (p, o, l)

def from_odds(o):
    if o <= 0:
        raise ValueError()
    p = float(o / (1 + o))
    l = log10(float(o))
    return (p, o, l)

def from_log_odds(l):
    o = nice_fraction(pow(10, l), 1)
    p = float(o / (1 + o))
    return (p, o, l)

def main(argv):
    argc = len(argv)
    if argc < 2 or argc >= 4:
        print 'usage: confidence probability|percentage|odds|log_odds [sig_digs]'
        return
    if argc >= 2:
        arg = argv[1]
    if argc >= 3:
        try:
            global sig_digs
            sig_digs = int(argv[2])
        except ValueError:
            print 'Invalid significant digits argument. Using default value of %d' % sig_digs
    try:
        if '%' in arg:
            (p, o, l) = from_probability(float(arg.replace('%', '')) / 100)
        elif ':' in arg:
            parts = arg.split(':')
            (p, o, l) = from_odds(nice_fraction(float(parts[0]), float(parts[1])))
        elif 'dB' in arg:
            (p, o, l) = from_log_odds(float(arg.replace('dB', '')) / 10)
        else:
            (p, o, l) = from_probability(float(arg))
        o = nice_fraction(o.numerator, o.denominator)
        print '%s = %s%% = %s:%s = %sdB' % (round_float(p), round_float(p * 100), o.numerator, o.denominator, round_float(l * 10))
        print 'Surprise if true: %s bits' % round_float(-log(p, 2))
        print 'Surprise if false: %s bits' % round_float(-log(1 - p, 2))
    except ValueError:
        print 'Invalid confidence argument. Accepted formats are:'
        print 'X -> probability (0, 1)'
        print 'X% -> percentage (0, 100)'
        print 'X:Y -> odds (0, infinity)'
        print 'XdB -> log odds (-infinity, infinity)'

main(sys.argv)
