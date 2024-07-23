interface CustomCardProps {
  number: number;
  title: string;
  description: string;
  link?:
    | {
        label: string;
        url: string;
      }
    | undefined;
}

export { CustomCardProps };
