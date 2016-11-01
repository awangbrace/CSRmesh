CREATE TABLE IF NOT EXISTS devices (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    deviceHash INTEGER,
    deviceID INTEGER,
    name TEXT,
    appearance INTEGER,
    modelHigh INTEGER,
    modelLow INTEGER,
    uuid BLOB,
    authCode INTEGER,
    isFavourite INTEGER,
    isAssociated INTEGER,

    model INTEGER,
    placeID INTEGER,
    FOREIGN KEY (model) REFERENCES models(_id),
    FOREIGN KEY (placeID) REFERENCES places(_id)
);

CREATE TABLE IF NOT EXISTS models (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    modelNumber INTEGER,
    modelInstance INTEGER,
    nAreaInstances INTEGER,
    areaIDs BLOB,

    deviceID INTEGER,
    FOREIGN KEY (deviceID) REFERENCES devices(_id)
);

CREATE TABLE IF NOT EXISTS areas (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    areaID INTEGER,
    name TEXT,

    placeID INTEGER,
    FOREIGN KEY (placeID) REFERENCES places(_id)
);

CREATE TABLE IF NOT EXISTS places (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    networkKey TEXT,
    iconID INTEGER,
    color INTEGER,
    hostControllerID INTEGER
);

CREATE TABLE IF NOT EXISTS settings (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    concurrentConnections INTEGER,
    listeningMode INTEGER,
    retryCount INTEGER,
    retryInterval INTEGER

    placeID INTEGER,
    FOREIGN KEY (placeID) REFERENCES places(_id)
);

INSERT INTO devices VALUES(1, 11, 111, "First Device", 11111, 111111, 1111111, null, null, 0, 1, 0, 0);
INSERT INTO devices VALUES(2, 22, 222, "Second Device", 22222, 222222, 2222222, null, null, 1, 0, 0, 0);

INSERT INTO settings VALUES(1, 11, 111, 1111, 11111);
INSERT INTO settings VALUES(2, 22, 222, 2222, 22222);