/* eslint-disable no-var */
var mockJsonViewer = jest.fn();
import React from 'react';
import { waitFor, render, screen } from '@testing-library/react';
import { VoteReceiptMock_Full_MediumAssurance } from 'test/mocks';
import { valueTransformerMap } from '../utils';

jest.mock('@textea/json-viewer', () => ({
  JsonViewer: mockJsonViewer,
}));

describe('utils:', () => {
  test('valueTransformerMap', async () => {
    const JsonViewerContentMock = 'JsonViewerContent';
    mockJsonViewer.mockReset();
    mockJsonViewer.mockReturnValue(JsonViewerContentMock);

    const voteProof = {
      transactionHash: VoteReceiptMock_Full_MediumAssurance?.merkleProof?.transactionHash,
      rootHash: VoteReceiptMock_Full_MediumAssurance?.merkleProof?.rootHash,
      steps: VoteReceiptMock_Full_MediumAssurance?.merkleProof?.steps,
      coseSignature: VoteReceiptMock_Full_MediumAssurance.coseSignature,
      cosePublicKey: VoteReceiptMock_Full_MediumAssurance.cosePublicKey,
    };

    expect(valueTransformerMap.id(VoteReceiptMock_Full_MediumAssurance.id)).toEqual('e51fdf09...4c836052b4f0');
    expect(valueTransformerMap.voterStakingAddress(VoteReceiptMock_Full_MediumAssurance.voterStakingAddress)).toEqual(
      'stake...pyqyg'
    );

    expect(screen.queryByText(JsonViewerContentMock)).toBeNull();

    render(<>{valueTransformerMap.voteProof(voteProof)}</>);
    await waitFor(() => {
      expect(mockJsonViewer).toBeCalledWith({ value: voteProof, enableClipboard: false }, {});
      expect(screen.queryByText(JsonViewerContentMock)).not.toBeNull();
    });
  });
});
