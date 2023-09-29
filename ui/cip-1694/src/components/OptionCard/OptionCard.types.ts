import React from 'react';

export interface OptionItem<T> {
  id: string;
  name: T;
  label: string;
  icon: React.ReactElement | null;
}

export interface OptionProps<T> {
  items: OptionItem<T>[];
  onChangeOption: (option: string) => void;
  disabled?: boolean;
  selectedOption: T;
}
