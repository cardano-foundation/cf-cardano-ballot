// TODO: lets' scope these under the component folder?
export default function ToggleButton(theme) {
  return {
    MuiToggleButton: {
      styleOverrides: {
        root: {
          '&.Mui-selected': {
            backgroundColor: theme.palette.primary.main,
            color: '#92ffc0',
          },
        },
      },
    },
  };
}
