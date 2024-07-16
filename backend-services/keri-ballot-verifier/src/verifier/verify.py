# -*- encoding: utf-8 -*-
"""
KERI
keri.app.verify module
"""
import falcon
from hio.base import doing
from hio.core import http
from hio.help import decking
from keri import help
from keri.app import oobiing, querying, storing, forwarding
from keri.core import eventing, routing, parsing
from keri.peer import exchanging
from . import controllers

logger = help.ogler.getLogger()

def setupVerifier(hby, hab, name, port, adminPort):
    doers = []

    cues = decking.Deck()
    queries = decking.Deck()

    rvy = routing.Revery(db=hby.db, cues=cues)
    kvy = eventing.Kevery(db=hby.db,
                          lax=True,
                          local=False,
                          rvy=rvy,
                          cues=cues)
    kvy.registerReplyRoutes(router=rvy.rtr)

    mbx = storing.Mailboxer(name=name, temp=hby.temp)
    forwarder = forwarding.ForwardHandler(hby=hby, mbx=mbx)
    exchanger = exchanging.Exchanger(hby=hby, handlers=[forwarder])
    parser = parsing.Parser(framed=True,
                            kvy=kvy,
                            exc=exchanger,
                            rvy=rvy)

    oobiery = oobiing.Oobiery(hby=hby)

    app = falcon.App(cors_enable=True)
    app.add_route("/", controllers.HttpEnd(ims=parser.ims))
    server = http.Server(port=port, app=app)

    adminApp = createAdminApp(hby=hby, hab=hab, queries=queries)
    adminServer = http.Server(port=adminPort, app=adminApp)

    doers.extend([
        ParserDoer(kvy=kvy, parser=parser),
        Querier(hby=hby, hab=hab, kvy=kvy, queries=queries),
        http.ServerDoer(server=server),
        http.ServerDoer(server=adminServer),
        *oobiery.doers
    ])

    return doers


def createAdminApp(hby, hab, queries):
    # Set up Falcon administrative application
    adminApp = falcon.App(cors_enable=True)
    adminApp.add_route("/oobi", controllers.OOBIEnd(hby=hby))
    adminApp.add_route("/keystate", controllers.KeyStateCreateEnd(hby=hby, hab=hab, queries=queries))
    adminApp.add_route("/keystate/{pre}", controllers.KeyStateQueryEnd(hby=hby, hab=hab))
    adminApp.add_route("/verify", controllers.VerificationEnd(hab=hab))
    adminApp.add_route("/health", controllers.HealthEnd())

    return adminApp


class Querier(doing.DoDoer):

    def __init__(self, hby, hab, queries, kvy):
        self.hby = hby
        self.hab = hab
        self.queries = queries
        self.kvy = kvy

        super(Querier, self).__init__(always=True)

    def recur(self, tyme, deeds=None):
        if self.queries:
            pre = self.queries.popleft()
            self.extend([querying.QueryDoer(hby=self.hby, hab=self.hab, pre=pre, kvy=self.kvy)])

        return super(Querier, self).recur(tyme, deeds)


class ParserDoer(doing.Doer):

    def __init__(self, kvy, parser):
        self.kvy = kvy
        self.parser = parser
        super(ParserDoer, self).__init__()

    def recur(self, tyme=None):
        done = yield from self.parser.parsator()
        return done