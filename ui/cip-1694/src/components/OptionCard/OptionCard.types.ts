interface OptionItem {
  label: string;
  icon: any;
}

interface OptionProps {
  items: OptionItem[];
  onChangeOption: (option: string) => void;
}

export type { OptionItem, OptionProps };
