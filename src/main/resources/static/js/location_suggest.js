let requestTimeoutHandle;

// Prevent adding the event listener before the html has loaded
window.addEventListener('load', function() {
    document.body.addEventListener('click', closeAllLists, true);
});

async function fetchAsync(url) {
    let response = await fetch(url);
    return await response.json();
}


function closeAllLists() {
    let x = document.getElementsByClassName("autocomplete-items");
    for (let i = 0; i < x.length; i++) {
        x[i].parentNode.removeChild(x[i]);
    }
}

async function request(inp, requestPath) {
    if (!inp.value) return false;

    let a = document.createElement("DIV");
    a.setAttribute("id", inp.id + " autocomplete-list");
    a.setAttribute("class", "autocomplete-items");

    inp.parentNode.appendChild(a);

    let b;
    b = document.createElement("DIV");
    b.innerHTML += "Loading..."
    a.appendChild(b);

    const results = await fetchAsync(requestPath + "?q=" + inp.value);

    a.removeChild(b);

    for (let i = 0; i < results.length; i++) {
        b = document.createElement("DIV");
        b.innerHTML += results[i].formatted;
        b.addEventListener("click", function (e) {
            closeAllLists();
            const address = results[i];
            const addressLine1 = [address.housenumber, address.street].join(' ').trim();
            document.getElementById("addressLine1").value = (addressLine1 !== ' ') ? addressLine1 : "";
            document.getElementById("suburb").value = (address.suburb) ? `${address.suburb}` : "";
            document.getElementById("city").value = (address.city) ? `${address.city}` : "";
            document.getElementById("postcode").value = (address.postcode) ? `${address.postcode}` : "";
            document.getElementById("country").value = (address.country) ? `${address.country}` : "";

            document.getElementById("suburb").dispatchEvent(new Event('input'))
            document.getElementById("city").dispatchEvent(new Event('input'))
            document.getElementById("postcode").dispatchEvent(new Event('input'))
            document.getElementById("country").dispatchEvent(new Event('input'))
        });
        a.appendChild(b);
    }
}

async function suggestAddress(requestPath) {
    closeAllLists();
    window.clearTimeout(requestTimeoutHandle);
    requestTimeoutHandle = setTimeout(async () => {
        await request(document.getElementById("addressLine1"), requestPath);
    }, 500)

}