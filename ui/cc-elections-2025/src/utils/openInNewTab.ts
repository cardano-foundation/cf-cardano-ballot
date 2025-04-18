export const openInNewTab = (url: string) => {
  // Ensure the URL is absolute
  const fullUrl =
    url.startsWith("http://") || url.startsWith("https://")
      ? url
      : url.startsWith("ipfs")
      ? `${import.meta.env.VITE_IPFS_GATEWAY}/${url.slice(7)}`
      : `https://${url}`;

  // eslint-disable-next-line no-console
  console.debug("Opening in new tab", fullUrl);

  // Open the URL in a new tab
  const newWindow = window.open(fullUrl, "_blank", "noopener,noreferrer");
  if (newWindow) newWindow.opener = null;
};
