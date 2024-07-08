from unittest.mock import MagicMock, patch

import falcon
import pytest
from falcon import testing
from keri import kering, core
from hio.help import decking
from types import SimpleNamespace

from verifier.verify import createAdminApp

oobi_params = {
    "oobi" : "sample_oobi"
}

verify_params = {
    "pre": "sample_aid",
    "signature": "sample_signature",
    "payload": "sample_payload"
}

invalid_params = {
    "invalid_param": "sample"
}

ksn_params = {
    "pre": "sample_aid"
}

response_no_params = "empty JSON body"
response_missing_params = ["required field", "missing from request"]

queries = decking.Deck()


@pytest.fixture
def mock_hby():
    # Mock the hby object
    mock = MagicMock()
    mock.db.roobi.get.return_value = None
    mock.db.oobis.put.return_value = None
    mock.db.knas.getItemIter.return_value = None
    mock.db.knas.rem.return_value = None
    mock.db.ksns.rem.return_value = None
    mock.db.kdts.rem.return_value = None
    return mock


@pytest.fixture
def mock_hab():
    # Mock the hab object
    mock = MagicMock()
    mock.kevers = {}
    return mock


@pytest.fixture
def client(mock_hby, mock_hab):
    adminApp = createAdminApp(mock_hby, mock_hab, queries)
    return testing.TestClient(adminApp)


class TestOOBIEnd:
    def test_get_oobi(self, client, mock_hby):
        mock_get = MagicMock()
        mock_get.return_value = MagicMock(cid="sample_cid")
        mock_hby.db.roobi.get = mock_get

        response = client.simulate_get('/oobi?url=sample_oobi')

        assert response.status == falcon.HTTP_200
        assert response.text == 'sample_cid'

    def test_get_oobi_not_found(self, client, mock_hby):
        mock_get = MagicMock()
        mock_get.return_value = None
        mock_hby.db.roobi.get = mock_get

        response = client.simulate_get('/oobi?url=sample_oobi_not_found')

        assert response.status == falcon.HTTP_404

    def test_get_oobi_no_params(self, client):
        response = client.simulate_get('/oobi')

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text

    def test_get_oobi_empty_params(self, client):
        response = client.simulate_get('/oobi?url=')

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text

    def test_get_oobi_invalid_params(self, client):
        response = client.simulate_get('/oobi?oobiurl=sampleurl')

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text

    def test_post_oobi(self, client, mock_hby):
        response = client.simulate_post('/oobi', json=oobi_params)

        # Retrieve the arguments that were passed to the put method
        kwargs = mock_hby.db.oobis.put.call_args.kwargs

        # Check the key and value arguments
        key = kwargs['keys'][0]
        value = kwargs['val']

        assert response.status == falcon.HTTP_202
        assert key == 'sample_oobi'
        from keri.db.basing import OobiRecord # When imported globally it raises a circular import error
        assert isinstance(value, OobiRecord)

    def test_post_oobi_no_params(self, client):
        response = client.simulate_post('/oobi')

        assert response.status == falcon.HTTP_400
        assert response_no_params in response.text

    def test_post_oobi_empty_params(self, client):
        response = client.simulate_post('/oobi', json={})

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text

    def test_post_oobi_invalid_params(self, client):
        response = client.simulate_post('/oobi', json=invalid_params)

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text


class TestVerificationEnd:
    def test_post_verify_success(self, client, mock_hab):
        mock_kever = MagicMock()
        mock_verfer = MagicMock()
        mock_verfer.verify.return_value = True
        mock_kever.verfers = [mock_verfer]
        mock_hab.kevers = {"sample_aid": mock_kever}

        with patch('keri.core.coring.Cigar', return_value=MagicMock(verfer=mock_verfer)):
            response = client.simulate_post('/verify', json=verify_params)

        assert response.status == falcon.HTTP_200
        assert response.text == 'Verification successful'

    def test_post_verify_unknown_aid(self, client, mock_hab):
        mock_hab.kevers = {}

        response = client.simulate_post('/verify', json=verify_params)

        assert response.status == falcon.HTTP_404
        assert "Unknown AID" in response.text

    def test_post_verify_invalid_signature(self, client, mock_hab):
        mock_kever = MagicMock()
        mock_verfer = MagicMock()
        mock_verfer.verify.return_value = False  # Simulate signature verification failure
        mock_kever.verfers = [mock_verfer]
        mock_hab.kevers = {"sample_aid": mock_kever}

        with patch('keri.core.coring.Cigar', return_value=MagicMock(verfer=mock_verfer)):
            response = client.simulate_post('/verify', json=verify_params)

        assert response.status == falcon.HTTP_400
        assert "Signature is invalid" in response.text

    def test_post_verify_invalid_signature_format(self, client, mock_hab):
        mock_kever = MagicMock()
        mock_verfer = MagicMock()
        mock_kever.verfers = [mock_verfer]
        mock_hab.kevers["sample_aid"] = mock_kever

        with patch('keri.core.coring.Cigar', side_effect=kering.ShortageError):
            response = client.simulate_post('/verify', json=verify_params)

        assert response.status == falcon.HTTP_400
        assert "Invalid signature format" in response.text

    def test_post_verify_no_params(self, client):
        response = client.simulate_post('/verify')

        assert response.status == falcon.HTTP_400
        assert response_no_params in response.text

    def test_post_verify_empty_params(self, client):
        response = client.simulate_post('/verify', json={})

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text

    def test_post_verify_invalid_params(self, client):
        response = client.simulate_post('/verify', json=invalid_params)

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text


class TestKeyStateCreateEnd:
    def test_post_ks_success(self, client, mock_hby, mock_hab):
        mock_kever = MagicMock()
        mock_hab.kevers = {"sample_aid": mock_kever}

        keys = ('sample_aid', 'EDioKSZWeNZE-kkW0lVZ5eKXOXGrfDDfuyI99HSaMZTU')
        saider = core.coring.Saider(qb64='ED3JCRbzw2KkY-w-Z81bqhu6U3OZ7Dq5szt5mX4bmnUI')

        mock_get = MagicMock()
        mock_get.return_value = [(keys, saider)]
        mock_hby.db.knas.getItemIter = mock_get

        response = client.simulate_post('/keystate', json=ksn_params)
        assert response.status == falcon.HTTP_202
        assert mock_hby.db.knas.rem.call_args.args[0] == keys
        assert mock_hby.db.ksns.rem.call_args.args[0] == (saider.qb64,)
        assert mock_hby.db.kdts.rem.call_args.args[0] == (saider.qb64,)
        assert queries.popleft() == "sample_aid"

    def test_post_ks_unknown_aid(self, client, mock_hab):
        mock_kever = MagicMock()
        mock_hab.kevers = {"different_aid": mock_kever}
        response = client.simulate_post('/keystate', json=ksn_params)
        assert response.status == falcon.HTTP_404
        assert "Unknown AID" in response.text

    def test_post_ks_no_params(self, client):
        response = client.simulate_post('/keystate')
        assert response.status == falcon.HTTP_400
        assert response_no_params in response.text

    def test_post_ks_empty_params(self, client):
        response = client.simulate_post('/keystate', json={})

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text

    def test_post_ks_invalid_params(self, client):
        response = client.simulate_post('/keystate', json=invalid_params)

        assert response.status == falcon.HTTP_400
        for substring in response_missing_params:
            assert substring in response.text


class TestKeyStateQueryEnd:
    def test_get_ks_complete(self, client, mock_hby, mock_hab):
        mock_kever = MagicMock()
        mock_kever.serder.said = "sample_aid"
        mock_hab.kevers = {"sample_aid": mock_kever}

        keys = ('sample_aid', 'EDioKSZWeNZE-kkW0lVZ5eKXOXGrfDDfuyI99HSaMZTU')
        saider = core.coring.Saider(qb64='ED3JCRbzw2KkY-w-Z81bqhu6U3OZ7Dq5szt5mX4bmnUI')

        mock_get_knas = MagicMock()
        mock_get_knas.return_value = [(keys, saider)]
        mock_hby.db.knas.getItemIter = mock_get_knas

        mock_get_ksns = MagicMock()
        mock_get_ksns.return_value = SimpleNamespace(**({"d": "sample_aid"}))  # Allows dot notation
        mock_hby.db.ksns.get = mock_get_ksns

        response = client.simulate_get('/keystate/sample_aid')
        assert response.status == falcon.HTTP_200
        assert "true" in response.text

    def test_get_ks_not_complete(self, client, mock_hby, mock_hab):
        mock_kever = MagicMock()
        mock_kever.serder.said = "sample_aid"
        mock_hab.kevers = {"sample_aid": mock_kever}

        keys = ('sample_aid', 'EDioKSZWeNZE-kkW0lVZ5eKXOXGrfDDfuyI99HSaMZTU')
        saider = core.coring.Saider(qb64='ED3JCRbzw2KkY-w-Z81bqhu6U3OZ7Dq5szt5mX4bmnUI')

        mock_get_knas = MagicMock()
        mock_get_knas.return_value = [(keys, saider)]
        mock_hby.db.knas.getItemIter = mock_get_knas

        mock_get_ksns = MagicMock()
        mock_get_ksns.return_value = None
        mock_hby.db.ksns.get = mock_get_ksns

        response = client.simulate_get('/keystate/sample_aid')
        assert response.status == falcon.HTTP_200
        assert "false" in response.text

    def test_get_ks_unknown_aid(self, client, mock_hab):
        mock_kever = MagicMock()
        mock_hab.kevers = {"different_aid": mock_kever}
        response = client.simulate_get('/keystate/sample_aid')
        assert response.status == falcon.HTTP_404
        assert "Unknown AID" in response.text
