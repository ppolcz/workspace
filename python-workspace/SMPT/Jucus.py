#!/usr/bin/env python
# -*- coding: utf-8 -*-

import smtplib, numpy, math
from django.utils.encoding import smart_str, smart_unicode

msg = "\r\n".join([
    "From: ppolcz@gmail.com",
    "To: pdpolcz@gmail.com",
    "Subject: Just a message",
    "",
    "Why, oh why"
    ])

gmail_user = 'ppolcz@gmail.com'
gmail_pwd = 'G_Esztergom991'

# server = smtplib.SMTP('smtp.gmail.com:587')
# server.ehlo()
# server.starttls()
# server.login(gmail_user,gmail_pwd)
# server.sendmail(fromaddr, toaddrs, msg)
# server.quit()
















#
# gorellanna@gmail.com
# kjulcsi98@gmail.com
# verbenyi.maria@gmail.com

nevek = [
    (u"Polcz Doró"        , u"kicsi feleségecském" , "polcz.dorottya@gmail.com "     , +1 ),
    (u"Polcz Peti"        , u"Petúr"               , "ppolcz@gmail.com"              , -1 ),
    (u"Kriston Ági"       , u"Ágóca"               , "kristonagnes21@gmail.com"      , +1 ),
    (u"Zsigmond Iringó"   , u"Iringócskácska"      , "zsim99@gmail.com"              , +1 ),
    (u"Angyal Marci"      , u"Marcibukovics"       , "angyal.marci@gmail.com"        , -1 ),
    (u"Varga Ági"         , u"Ágóca"               , "varga.agnes.katalin@gmail.com" , +1 ),
    (u"Szikora Ágoston"   , u"Ágó"                 , "sziago12@gmail.com"            , -1 ),
    (u"Bede-Fazekas Luca" , u"Lucus"               , "calulu1998@gmail.com"          , +1 ),
    (u"Palotai Bernát"    , u"Börni"               , "palotaibezs@gmail.com"         , -1 ),
    (u"Károlyfi Hanna"    , u"Hannuska"            , "hanna.karolyfi1999@gmail.com"  , +1 ),
    (u"Csörgő Bori"       , u"Borikám"             , "csrgbori@gmail.com"            , +1 ),
    (u"Sisa Márk"         , u"Sisa Úr"             , "sisamarkadam@gmail.com"        , -1 ),
    (u"Ágoston Álmos"     , u"Álmos"               , "agoston.almos@gmail.com"       , -1 ),
    (u"Tamás Erzsi"       , u"Erzsi"               , "erzsi222@gmail.com"            , +1 ),
    ]
N = len(nevek)
print("Résztvevők száma: " + str(N))

def measure(perm):
    s = 0
    for i in range(N):
        s = s + nevek[i][3] * nevek[perm[i]][3]
    return s

SUM = 0
for i in range(N):
    SUM = SUM + nevek[i][3]

legkisebb_realis = - ( N - round(SUM / 2 + 1) ) + round(SUM) + 1

print("Legkisebb mérték, amit jó lenne elérni: " + str(legkisebb_realis))


ok = False
legkisebb_mertek = N
legkisebb_perm = None

MAX_ITER = 100000
it = 0
while legkisebb_mertek > legkisebb_realis and it < MAX_ITER:
    it = it + 1
    ok = True
    perm = numpy.random.permutation(N)

    for i in range(N):
        if perm[i] == i:
            ok = False
            break

    if perm[0] == 1 or perm[1] == 0 or perm[0] == 2:
        ok = False

    if not ok:
        continue

    ossz = measure(perm)
    if ossz < legkisebb_mertek:
        legkisebb_mertek = ossz
        legkisebb_perm = perm

    # if le

perm = legkisebb_perm

print("Permutáció: " + str(perm))
print("Permutáció mértéke: " + str(legkisebb_mertek))


print("")
print("Sorsolás eredménye: ")
print("------------------- ")
for i in range(N):
    print(nevek[i][0] + " - " + nevek[perm[i]][0])
print("------------------- ")
exit(0)

if legkisebb_mertek > legkisebb_realis:
    exit(0)


template = "\r\n".join([
    u"From: %s",
    u"To: %s",
    u"Subject: %s",
    u"",
    u"Kedves %s!",
    u"",
    # u"Automatikus sorsolás során %s lett a szerencsés kiválasztott, akit megajándékozhatsz!",
    # u"",
    # u"Áldott Adventi készületet majd Karácsonyt kívánva üdvözöl:",
    # u"Jucus.py (a mesterséges intelligencia megszemélysített Burgundi csoportos képviselője),",
    # u"és a Polcz család",
    # u"",
    # u"",
    u"Ez még egyelőre csak egy próba-email, hogy mindenkinek elmegy-e.",
    u"",
    u"Üdv: Peti",
    ])


# SMTP_SSL Example
server_ssl = smtplib.SMTP_SSL("smtp.gmail.com", 465)
server_ssl.ehlo() # optional, called by login()
server_ssl.login(gmail_user, gmail_pwd)

for i in range(N):
    ajandekozo_mail = nevek[i][2]
    ajandekozo_nev = nevek[i][1]
    ajandekozott_nev = nevek[perm[i]][0]

    mail = template % (gma, u"Burgundi karácsonyi ajándékozás", ajandekozo_nev)

    # print(" ")
    # print(smart_str(mail))
    # print(" ")

    continue
    server_ssl.sendmail(gmail_user, ajandekozo_mail, smart_str(mail))

#server_ssl.quit()
server_ssl.close()
print 'Mails were sent successfully!'