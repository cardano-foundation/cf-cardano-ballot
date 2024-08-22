import json
import falcon
from keri import kering
from keri.app import httping
from keri.core import coring, eventing
from keri.db import basing
from keri.help import helping, nowIso8601


class HttpEnd:
    def __init__(self, ims):
        self.ims = ims

    def on_post(self, req, resp):
        cr = httping.parseCesrHttpRequest(req=req)
        sadder = coring.Sadder(ked=cr.payload, kind=eventing.Serials.json)
        msg = bytearray(sadder.raw)
        msg.extend(cr.attachments.encode("utf-8"))

        self.ims.extend(msg)

        if sadder.proto not in ("ACDC",):
            if sadder.ked["t"] in (coring.Ilks.qry,) and sadder.ked["r"] in ("mbx",):
                raise falcon.HTTPConflict(title="no mailbox support")

        resp.status = falcon.HTTP_204


class OOBIEnd:
    def __init__(self, hby):
        self.hby = hby

    def on_get(self, req, resp):
        # This should be a path param but is causing issues, query will do.
        oobi = req.params.get('url')

        if oobi is None or oobi == "":
            raise falcon.HTTPBadRequest(description=f"required field url missing from request")

        result = self.hby.db.roobi.get(keys=(oobi,))
        if result:
            resp.status = falcon.HTTP_200
            resp.text = result.cid
        else:
            resp.status = falcon.HTTP_404

    def on_post(self, req, resp):
        oobi = getRequiredParam(req.get_media(), 'oobi')
        obr = basing.OobiRecord(date=helping.nowIso8601())
        self.hby.db.oobis.put(keys=(oobi,), val=obr)
        resp.status = falcon.HTTP_202


class KeyStateCreateEnd:
    def __init__(self, hby, hab, queries):
        self.hby = hby
        self.hab = hab
        self.queries = queries

    def on_post(self, req, resp):
        body = req.get_media()
        pre = getRequiredParam(body, 'pre')

        try:
            self.hab.kevers[pre]
        except KeyError as _:
            resp.status = falcon.HTTP_404
            resp.text = f"Unknown AID {pre}"
            return

        for (keys, saider) in self.hby.db.knas.getItemIter(keys=(pre,)):
            self.hby.db.knas.rem(keys)
            self.hby.db.ksns.rem((saider.qb64,))
            self.hby.db.kdts.rem((saider.qb64,))

        self.queries.append(pre)
        resp.status = falcon.HTTP_202


class KeyStateQueryEnd:
    def __init__(self, hby, hab):
        self.hby = hby
        self.hab = hab

    def on_get(self, _, resp, pre):
        try:
            kever: eventing.Kever = self.hab.kevers[pre]
        except KeyError as _:
            resp.status = falcon.HTTP_404
            resp.text = f"Unknown AID {pre}"
            return

        ksn = None
        for (_, saider) in self.hby.db.knas.getItemIter(keys=(pre,)):
            ksn = self.hby.db.ksns.get(keys=(saider.qb64,))
            break

        resp.status = falcon.HTTP_200
        resp.content_type = "application/json"
        if ksn and ksn.d == kever.serder.said:
            resp.data = json.dumps({"complete": True}).encode("utf-8")
        else:
            resp.data = json.dumps({"complete": False}).encode("utf-8")


class VerificationEnd:
    def __init__(self, hab):
        self.hab = hab

    def on_post(self, req, resp):
        body = req.get_media()
        pre = getRequiredParam(body, 'pre')
        signature = getRequiredParam(body, 'signature')
        payload = getRequiredParam(body, 'payload')

        try:
            kever = self.hab.kevers[pre]
        except KeyError as _:
            resp.status = falcon.HTTP_404
            resp.text = f"Unknown AID {pre}, please ensure corresponding OOBI has been resolved"
            return
        verfers = kever.verfers

        try:
            cigar = coring.Cigar(qb64=signature)
        except (ValueError, kering.ShortageError) as e:
            resp.status = falcon.HTTP_400
            resp.text = f"Invalid signature format (single sig only supported) - error: {e}"
            return

        # Single sig support
        cigar.verfer = verfers[0]
        if cigar.verfer.verify(cigar.raw, str.encode(payload)):
            resp.status = falcon.HTTP_200
            resp.text = "Verification successful"
        else:
            resp.status = falcon.HTTP_400
            resp.text = f"Signature is invalid"


class HealthEnd:
    def on_get(self, _, resp):
        resp.status = falcon.HTTP_OK
        resp.media = {"message": f"Health is okay. Time is {nowIso8601()}"}


def getRequiredParam(body, name):
    param = body.get(name)
    if param is None:
        raise falcon.HTTPBadRequest(description=f"required field '{name}' missing from request")
    if not isinstance(param, str):
        raise falcon.HTTPBadRequest(description=f"field '{name}' must be a string value")

    return param
