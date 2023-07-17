import React from 'react';

interface OptionItem {
  label: string;
  icon: React.ReactElement | null;
}

interface OptionProps {
  items: OptionItem[];
  onChangeOption: (option: string) => void;
}

export type { OptionItem, OptionProps };
