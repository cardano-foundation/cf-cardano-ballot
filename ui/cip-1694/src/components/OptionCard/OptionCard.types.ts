import React from 'react';

interface OptionItem {
  label: 'yes' | 'no' | 'abstain';
  icon: React.ReactElement | null;
}

interface OptionProps {
  items: OptionItem[];
  onChangeOption: (option: string) => void;
  disabled?: boolean;
  selectedOption: string;
}

export type { OptionItem, OptionProps };
