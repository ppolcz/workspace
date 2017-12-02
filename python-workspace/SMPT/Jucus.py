#!/usr/bin/env python
# -*- coding: utf-8 -*-

import smtplib, numpy
# fromaddr = 'ppolcz@gmail.com'
# toaddrs  = 'ppolcz@gmail.com'

from django.utils.encoding import smart_str, smart_unicode

a = u'\xa1'
print smart_str(a)


szerencsesek = [  ]


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

nevek = [
  (u"Zsigmond Iringó", u"Iringócskácska"),
  (u"Angyal Marci", u"Marcibukovics"),
  (u"Varga Ági", u"Ágóca"),
  (u"Szikora Ágoston", u"Ágó"),
  (u"Bede-Fazekas Luca", u"Lucus"),
  (u"Kriston Ági", u"Ágóca"),
  (u"Palotai Bernát", u"Börni"),
  (u"Károlyfi Hanna", u"Hannuska"),
  (u"Csörgő Bori", u"Borikám"),
  (u"Sisa Márk", u"Sisa Úr"),
  (u"Ágoston Álmos", u"Álmos"),
  (u"Tamás Erzsi", u"Erzsi"),
  (u"Polcz Peti", u"Petúr"),
  (u"Polcz Doró", u"kicsi feleségecském"),
  ]

N = len(nevek)
perm = numpy.random.permutation(N)

for i in range(N):
  print(nevek[i][0] + " - " + nevek[perm[i]][0])

for a,b in nevek:
  print(a)

exit(0)

template = "\r\n".join([
  u"From: %s",
  u"To: %s",
  u"Subject: %s",
  u"",
  u"Kedves %s!",
  u"",
  u"Automatikus sorsolás során %s lett a szerencsés kiválasztott, akit megajándékozhatsz!",
  u"",
  u"Áldott Adventi készületet majd Karácsonyt kívánva üdvözöl:",
  u"Jucus.py (a mesterséges intelligencia megszemélysített Burgundi csoportos képviselője)",
  u""
  ])

ajandekozo_mail = "pdpolcz@gmail.com"
ajandekozo_nev = "Peti"
ajandekozott_nev = "Jucus"

mail = template % (gmail_user, ajandekozo_mail, u"Burgundi karácsonyi ajándékozás", ajandekozo_nev, ajandekozott_nev)

print(smart_str(mail))

# SMTP_SSL Example
server_ssl = smtplib.SMTP_SSL("smtp.gmail.com", 465)
server_ssl.ehlo() # optional, called by login()
server_ssl.login(gmail_user, gmail_pwd)

# ssl server doesn't support or need tls, so don't call server_ssl.starttls()
server_ssl.sendmail(gmail_user, "ppolcz@gmail.com", smart_str(mail))

# ssl server doesn't support or need tls, so don't call server_ssl.starttls()
server_ssl.sendmail(gmail_user, "pdpolcz@gmail.com", smart_str(mail))

#server_ssl.quit()
server_ssl.close()
print 'successfully sent the mail'