const popupWindow = (url, title, w, h) => {
    const left = (window.screen.width - w) / 2;
    const top = (window.screen.height - h) / 4;
    const newWindow = window.open(url, title,
        `
    scrollbars=yes,
    width=${w}, 
    height=${h}, 
    top=${top}, 
    left=${left}
    `
    )

    if (window.focus)
        newWindow.focus();
    return newWindow;
}

export default popupWindow;