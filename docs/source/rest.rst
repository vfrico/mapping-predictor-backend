.. _restservice:


REST Service
============


Annotations
-----------

.. http:get:: /annotations

    Download all the annotations as a JSON or CSV.

    On the headers of the HTTP request, it is possible to specify the format with:

    .. sourcecode:: text

        Accept: text/plain
        Accept: application/json

    :param str langa: Query param for Language A (Usually 'en')
    :param str langb: Query param for  Language B (Use the shortest ISO code available)
    :statuscode 400: Parameters has not been correctly established
    :statuscode 200: Annotations downloaded successfully



.. http:get:: /annotations/(int:annotation_id)/

    Get a full annotation. It includes all the attributes used to train the classifier.

    **Sample request and response**

    :http:get:`/annotations/1/`

    .. sourcecode:: json

        {
            "attributeA": "nationality",
            "attributeB": "lugar_de_fallecimiento",
            "c1": 1.0,
            "c2": 1.0,
            "c3a": 1.0,
            "c3b": 1.0,
            "classA": "http://dbpedia.org/ontology/Scientist",
            "classB": "http://dbpedia.org/ontology/Scientist",
            "domainPropA": "dbo:Person",
            "domainPropB": "dbo:Person",
            "langA": "en",
            "langB": "es",
            "m1": 621,
            "m2": 709,
            "m3": 719,
            "m4a": 643,
            "m4b": 493,
            "m5a": 719,
            "m5b": 719,
            "propA": "dbo:nationality",
            "propB": "dbo:deathPlace",
            "rangePropA": "dbo:Country",
            "rangePropB": "dbo:Place",
            "tb1": 0,
            "tb10": 1,
            "tb11": 0,
            "tb2": 0,
            "tb3": 1,
            "tb4": 1,
            "tb5": 1,
            "tb6": 1,
            "tb7": 1,
            "tb8": 1,
            "tb9": 0,
            "templateA": "Infobox_scientist",
            "templateB": "Ficha_de_científico",
            "id": 15098
        }

    :param int annotation_id: Unique *annotation_id*
    :statuscode 200: Returns all information about an annotation.
    :statuscode 404: The annotation can't be found.


.. http:post:: /annotations

    Add an annotation to the dataset. This endpoint should be used only by the service that generates annotations once
    a dbpedia release has been published. It is important to submit also the langA and langB parameters.

    **Sample request and response**

    :http:post:`/annotations`

    .. sourcecode:: json

        {
            "attributeA": "nationality",
            "attributeB": "lugar_de_fallecimiento",
            "c1": 1.0,
            "c2": 1.0,
            "c3a": 1.0,
            "c3b": 1.0,
            "classA": "http://dbpedia.org/ontology/Scientist",
            "classB": "http://dbpedia.org/ontology/Scientist",
            "domainPropA": "dbo:Person",
            "domainPropB": "dbo:Person",
            "langA": "en",
            "langB": "es",
            "m1": 621,
            "m2": 709,
            "m3": 719,
            "m4a": 643,
            "m4b": 493,
            "m5a": 719,
            "m5b": 719,
            "propA": "dbo:nationality",
            "propB": "dbo:deathPlace",
            "rangePropA": "dbo:Country",
            "rangePropB": "dbo:Place",
            "tb1": 0,
            "tb10": 1,
            "tb11": 0,
            "tb2": 0,
            "tb3": 1,
            "tb4": 1,
            "tb5": 1,
            "tb6": 1,
            "tb7": 1,
            "tb8": 1,
            "tb9": 0,
            "templateA": "Infobox_scientist",
            "templateB": "Ficha_de_científico",
        }


    :statuscode 201: The request has been accepted by the system and a new annotation is stored on DB.




.. http:get:: /annotations/(int:annotation_id)/helpers

    Get a full annotation. It includes all the attributes used to train the classifier.

    **Sample request and response**

    :http:get:`/annotations/26/`

    .. sourcecode:: json

        {
            "relatedTriples": [
                {
                    "object": "http://dbpedia.org/resource/Atlantic/Azores",
                    "predicate": "http://dbpedia.org/ontology/daylightSavingTimeZone",
                    "subject": "http://dbpedia.org/resource/Azores"
                },
                {
                    "object": "http://dbpedia.org/resource/Western_European_Summer_Time",
                    "predicate": "http://dbpedia.org/ontology/daylightSavingTimeZone",
                    "subject": "http://dbpedia.org/resource/Madeira"
                }
            ]
        }


    :param int annotation_id: Unique *annotation_id*
    :statuscode 200: Returns all the available helpers for one annotation
    :statuscode 404: The annotation can't be found.


.. http:post:: /annotations/classify

    Classifies annotations of a given language pair

    :statuscode 201: Classification of the instances successfully added to the database.
                     The body usually contains helpful information.


.. http:post:: /annotations/(int:annotation_id)/vote

    Add a vote for the annotation_id. There are two types of vote allowed: `CORRECT_MAPPING` or `WRONG_MAPPING`.


    **Sample request**

    The JSON of the body contains the minimal data to detect which mapping are referring to and which is the user that
    is voting to it.

    The `Authorization` header should provide the same Authentication token of the username on the body.

    :http:put:`/annotations/1/votes`

    .. sourcecode:: json

        {
            "vote": "CORRECT_MAPPING",
            "annotationId": 18606,
            "user": {
                "username": "default"
            }
        }

    :statuscode 404: The mapping or the user can not be found
    :statuscode 201: Vote has been accepted
    :statuscode 401: The token is not correct


.. http:post:: /annotations/(int:annotation_id)/lock

    Add a lock to the annotation. This will inform other users that the template is going to be edited and could
    incur on concurrent modifications. The lock will be held for one week.

    **Sample request**

    The JSON of the body contains the minimal data to detect which mapping are referring to and which is the user that
    is locking to it.

    The `Authorization` header should provide the same Authentication token of the username on the body.

    :http:put:`/annotations/1/votes`

    .. sourcecode:: json

        {
            "locked": true,
            "dateStart": 1533729222000,
            "dateEnd": 1533730222000,
            "user": {
                "username": "default"
            }
        }

    :statuscode 404: The mapping or the user can not be found
    :statuscode 201: Lock has been added
    :statuscode 401: The token is not correct


.. http:delete:: /annotations/(int:annotation_id)/lock

    Deletes the lock. Intended to use when the user has ended to edit the mapping on DBpedia mappings wiki.
    It also needs the Authorization header with the corresponding token.

    :statuscode 404: The mapping or the user can not be found
    :statuscode 204: Lock has been deleted
    :statuscode 401: The token is not correct



Installation
------------
The installation resource contains endpoints that should only be called with purposes of regenerating the database.


.. http:post:: /installation/createtables

    Create the main structure on the database. It uses SQL files that has internally the backend source code.

    It will also create sample users and add some SQL constants.

    :statuscode 201: The operation of creating tables was successful.



.. http:post:: /installation/addAllCSV

    Adds default CSV files that are included on the source code. It contains annotations for four different
    language pairs: English - Spanish, English - Greek, English - Dutch and Spanish - German

    :statuscode 201: The operation of adding more annotations was successful


.. http:post:: /installation/fromCSV

    Upload a CSV file that contains annotations.

    The POST request should contain a `multipart/form-data` Content-Type header, and send the CSV file
    named `file` on the multipart form. The language pair should be sumbitted as query params: langa and langb


    :param str langa: Query param for Language A (Usually 'en')
    :param str langb: Query param for Language B (Use the shortest ISO code available)
    :statuscode 201: The operation of adding more annotations was successful



Templates
----------

The templates resource allows to access the annotations with a more organised way.

.. http:get:: /templates/langPairs

    Returns a list of lang pairs available on the system.

    **Sample response**

    .. sourcecode:: json

        [
            {
                "langA": "en", "langB": "es"
            },
            {
                "langA": "es", "langB": "de"
            },
            {
                "langA": "en", "langB": "el"
            },
            {
                "langA": "en", "langB": "nl"
            }
        ]


    :statuscode 200: A list was successfully returned



.. http:get:: /templates

    Returns a list of all templates given a lang pair

    **Sample response**

    .. sourcecode:: json

        [
            {
                "allAnnotations": 50,
                "correctAnnotations": 44,
                "lang": "es",
                "template": "Ficha_de_sencillo",
                "templateUsages": 0,
                "wrongAnnotations": 6
            },
            {
                "allAnnotations": 129,
                "correctAnnotations": 104,
                "lang": "es",
                "template": "Ficha_de_deportista",
                "templateUsages": 0,
                "wrongAnnotations": 25
            }
        ]

    :param str langa: Query param for Language A (Usually 'en')
    :param str langb: Query param for Language B (Use the shortest ISO code available)
    :statuscode 200: A list of templates. If no language is available, an emtpy list will be returned


.. http:get:: /templates/(str:lang)/(str:template)

    Get all the information once a template and a language is given. Gets also the available annotations.

    **Sample response**

    .. sourcecode:: json

        {
            "allAnnotations": 106,
            "annotations": [],
            "correctAnnotations": 45,
            "lang": "es",
            "locks": [],
            "template": "Ficha_de_entidad_subnacional",
            "templateUsages": 113146,
            "wrongAnnotations": 61
        }

    :param str lang: Path param of the language of the template
    :param str template: Path param of the template to be consulted
    :statuscode 200: All the information


Users
-----

.. http:post:: /users

    Insert new user on database. Should provide at least username, email and password. If no role is provided,
    it will be used the default one: "NO_ROLE"

    **Sample body**
    .. sourcecode:: json

        {
            "username": "test",
            "email": "vfrico@gmail.com",
            "password_md5": "237289476238",
            "role": {"ADMIN" | "ANNOTATOR" | "MAPPER" | "NO_ROLE"} # Choose only one
        }

    :statuscode 201: User created. On the body is returned the full user object


.. http:get:: /users/(str:username)

    Obtains all the user information available.

    :param str username: The username to be used on the request
    :statuscode 200: Returns the user object
    :statuscode 404: No username found


.. http:post:: /users/(str:username)/login

    Get the user information with the token field filled with a JWT token. It should be included on all requests
    as Authorization header:


    **Sample request body**

    *POST /users/default/login*

    .. sourcecode:: json

        {
            "password_md5": "default",
            "username": "default"
        }

    The response will contain the user object


    :param str username: The username to be used on the request
    :statuscode 200: User is logged in on the system
    :statuscode 404: Username can not be found



.. http:post:: /users/(str:username)/logout

    Deletes the token on the database

    **Sample request body**

    *POST /users/default/logout*

    .. sourcecode:: json

        {
            "username": "default"
        }


    :param str username: The username to be used on the request
    :statuscode 400: Username is not correct or auth token is not correct
    :statuscode 204: Token deleted successfully


.. http:put:: /users/(str:username)

    Edit information of the user. Currently it only supports change user role and change password. Submit on the
    body as many fields as you want to modify.

    **Sample request body**

    *PUT /users/default*

    .. sourcecode:: json

        {
            "role": "ADMIN"
        }

    :param str username: The username to be used on the request
    :statuscode 404: Username is not correct or auth token is not correct
    :statuscode 200: User changed successfully



.. http:delete:: /users/(str:username)

    Deletes the user on the database


    :param str username: The username to be used on the request
    :statuscode 404: Username is not correct or auth token is not correct
    :statuscode 204: User deleted successfully