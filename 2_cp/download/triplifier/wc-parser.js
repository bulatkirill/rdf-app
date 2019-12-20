const fs = require('fs');
const RDFBuilder = require('./RDFBuilder');

const SCHEMA = 'schema';
const SKOS = 'skos';
const KB_RESOURCE = 'kbr';
const KB_LOOKUPS_WC_PRICE = 'kblwcp';
const KB_RESOURCE_WC_PRICE = 'kbrwcp';
const KB_RESOURCE_WC = 'kbrwc';


function parseDataWc(dataset, resultFileName) {
    const converted = JSON.parse(dataset);
    const features = converted['features'];
    const rdfBuilder = new RDFBuilder();

    rdfBuilder.addPrefixes([
        {prefix: 'rdf', iri: 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'},
        {prefix: 'rdfs', iri: 'http://www.w3.org/2000/01/rdf-schema#'},
        {prefix: KB_RESOURCE_WC, iri: 'http://www.kyrylo.bulat.com/resource/wc/'},
        {prefix: KB_LOOKUPS_WC_PRICE, iri: 'http://www.kyrylo.bulat.com/cisleniky/wcprice'},
        {prefix: KB_RESOURCE_WC_PRICE, iri: 'http://www.kyrylo.bulat.com/resource/wcprice/'},
        {prefix: KB_RESOURCE, iri: "http://www.kyrylo.bulat.com/resource/"},
        {prefix: SCHEMA, iri: "http://schema.org/"},
        {prefix: SKOS, iri: "http://www.w3.org/2004/02/skos/core#"}
    ]);

    let cenaIndex = 0;
    const cenaIndexMap = {};

    rdfBuilder.addLookup(KB_LOOKUPS_WC_PRICE, 'Cislenik cen na toalety');

    features.forEach((feature, index) => {
        const properties = feature['properties'];
        const geometry = feature['geometry'];
        const longitude = geometry['coordinates'][0];
        const latitude = geometry['coordinates'][1];

        const cena = properties['CENA'];
        if (cenaIndexMap[cena] === undefined) {
            cenaIndexMap[cena] = cenaIndex++;
        }
        const cenaSubject = `${KB_RESOURCE_WC_PRICE}:${cenaIndexMap[cena]}`;
        rdfBuilder.addConceptToLookup(KB_LOOKUPS_WC_PRICE, cenaSubject, cena, cena);

        const subject = `${KB_RESOURCE_WC}:${index}`;
        const statements = {};
        statements['a'] = 'schema:PublicToilet';
        statements['kbr:objectid'] = properties['OBJECTID'];
        let address = escapeInvalidCharacters(properties['ADRESA']);
        if (address !== " ") {
            statements['schema:address'] = `"${address}"@cs`;
        }
        let openingHours = escapeInvalidCharacters(properties['OTEVRENO']);
        if (openingHours !== " ") {
            statements['kbr:openingHours'] = `"${openingHours}"@cs`;
        }
        statements['kbr:cena'] = cenaSubject;
        statements['schema:geo'] = {
            'a': 'schema:GeoCoordinates',
            'schema:latitude': latitude,
            'schema:longitude': longitude
        };
        rdfBuilder.addStatement(subject, statements);
    });
    fs.writeFileSync(resultFileName, rdfBuilder.build());
}

function escapeInvalidCharacters(text) {
    text = escapeDoubleQuote(text);
    return escapeNewLine(text);
}

function escapeDoubleQuote(text) {
    if (text !== null && text !== undefined) {
        return text.replace(/"/g, '\\"');
    }
    return text;
}

function escapeNewLine(text) {
    if (text !== null && text !== undefined) {
        return text.replace(/\r\n/g, ' ');
    }
    return text;
}

const datasetWc = fs.readFileSync('./dataset_wc.json', 'utf8');
parseDataWc(datasetWc, 'result_wc.ttl');
