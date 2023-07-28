import React from 'react';
import { Proposal } from 'types/types';

export interface OptionItem {
  label: Proposal;
  icon: React.ReactElement | null;
}

export interface OptionProps {
  items: OptionItem[];
  onChangeOption: (option: string) => void;
  disabled?: boolean;
  selectedOption: string;
}
