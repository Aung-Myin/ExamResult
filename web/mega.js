function hideByClass(clsName) {
    document.querySelectorAll('.' + clsName).forEach(e => {
        e.style.display = 'none';
    })
}

var cls = ['logo-container', 'viewer-top-bl'];
for (var k in cls) {
    hideByClass(cls[k]);
}

function fuckAds() {
    try {
        var rs = {};
        var ts = 15807e5;
        var now = Date.now() / 1e3;
        var pra = JSON.parse(localStorage.pra || '{}');
        var ids = Object.keys(pra);
        for (var i = ids.length; i--;) {
            var t = pra[ids[i]] + ts;

            if (now > t + 3600) {
                delete pra[ids[i]];
            } else {
                rs[ids[i]] = t;
            }
        }

        var req = {
            a: 'pra',
            rs: rs,
            d: d ? 1e3 : undefined
        };

        var tmp = String(page).split('!').map(function (s) {
            return s.replace(/[^\w-]+/g, "");
        });

        var ph = tmp[1];
        if (ph) {
            req.ph = ph;
        }

        M.req(req).always(function (res) {
            pra[res.id] = now - ts | 0;
            localStorage.pra = JSON.stringify(pra);
            console.log(localStorage.pra);
        });
        clearInterval(f);
    } catch (e) {
        console.log(e);
    }
}

fuckAds();
var f = setInterval(fuckAds, 1000);