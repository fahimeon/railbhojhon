import urllib.request, json, urllib.parse
titles=['Biryani', 'Nihari', 'Roast chicken', 'Shami kebab', 'Pilaf', 'Korma', 'Rice pudding']
for t in titles:
    url = f'https://en.wikipedia.org/w/api.php?action=query&titles={urllib.parse.quote(t)}&prop=pageimages&format=json&pithumbsize=500'
    res = json.loads(urllib.request.urlopen(url).read())
    pages = res['query']['pages']
    for p in pages.values():
        print(f"{t}: {p.get('thumbnail', {}).get('source', 'No img')}")
