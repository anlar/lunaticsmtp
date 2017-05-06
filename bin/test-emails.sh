#! /usr/bin/env sh

#java -jar build/libs/LunaticSMTP-v0.3.0.jar -s -j &

#sleep 5

sendemail -s localhost:2525 \
 -t tewi.inabe@eientei.com \
 -f reisen.udongein@moon.com \
 -u Hello \
 -o message-content-type=text \
 -m test

#sleep 30

sendemail -s localhost:2525 \
 -t kaguya@moon.com \
 -f reisen.udongein@moon.com \
 -u RE: Holidays \
 -o message-content-type=text \
 -m test

#sleep 40

sendemail -s localhost:2525 \
 -t kaguya@moon.com \
 -f reisen.udongein@moon.com \
 -u RE: RE: RE: RE: RE: RE: RE: RE: RE: RE: RE: RE: RE: RE: RE: RE: Holidays  \
 -o message-content-type=text \
 -m test

#sleep 50

touch /tmp/dm.sh

sendemail -s localhost:2525 \
 -t 'Tewi Inaba <tewi.inaba@eientei.com>' \
 -f 'Reisen Udongein Inaba <reisen.udongein@moon.com>' \
 -u News \
 -o message-content-type=html \
 -a /tmp/dm.sh \
 -m '<p>Hello,</p>
<p>Test <b>newsletter</b>, with <a href="https://github.com/anlar/LunaticSMTP">links</a> and images:</p>
<p><img src="https://en.touhouwiki.net/images/e/e0/GoMSigil-Reisen.jpg" height="200" width="200"></p>'
