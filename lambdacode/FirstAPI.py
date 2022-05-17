# Tanner Selvig
# First database api


import psycopg2 # Postgres connection library
import logging # Library to log errors
import sys

queryall = "/queryall"
querycol = "/querycol"
insertlink = "/insert"
deletelink = "/delete"
updatelink = "/update"

allowedChars = set('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890- ')

validCols = ["id", "quantity", "name", "groupid", "marked", "timestamp", "upc", "archived", "alias", "store", "uom", "date"]
validTables = ["ids", "inventory", "items", "lists", "shoppinglist", "list_items"]

logger = logging.getLogger()
logger.setLevel(logging.INFO)

#Connecting to DB
try:
    # logger.info("Made it to before try")
    conn = psycopg2.connect(
        host="stockerdb.cqcsfxgippqz.us-west-1.rds.amazonaws.com",
        port="5432",
        database="postgres",
        user="postgres",
        password="326f439b409df88ef3f4ff9b251307f050a1bcfd")
    # logger.info("Made it to end of try")
except (Exception, psycopg2.DatabaseError) as error:
    logger.error("ERROR: Couldn't connect to db")
    logger.error(error)
    # logger.info("Made it to before exit in catch")
    sys.exit()
    

# Handler function
def lambda_handler(event, context):
    logger.info(event)
    # Creating cursor to execute commands
    cur = conn.cursor()
    if event:
        if 'rawPath' in event:
            # Paths like get, post, etc
            if event['rawPath'] == queryall:
                if 'queryStringParameters' in event:
                    # Checking to see which table is being queried
                    if 'table' in event['queryStringParameters']:
                        result = queryAll(event['queryStringParameters']['table'], conn)
                        return result
                    else:
                        return "No table specified"
                else:
                    return "No parameters"
            elif event['rawPath'] == querycol:
                if 'queryStringParameters' in event:
                    if 'table' in event['queryStringParameters']:
                        if 'col' in event['queryStringParameters']:
                            result = queryCol(event['queryStringParameters']['table'], event['queryStringParameters']['col'], conn)
                            return result
                        else:
                            return "No column specified"
                    else:
                        return "No table specified"
                else:
                    return "No parameters"
            elif event['rawPath'] == insertlink:
                if 'queryStringParameters' in event:
                    if 'table' in event['queryStringParameters']:
                        if 'body' in event:
                            try:
                                values = event['body'].rsplit(",") 
                                logger.info(values)
                            except:
                                return "Values deliminated incorrectly, remember to commas between values with no spaces"
                            result = insertQuery(event['queryStringParameters']['table'], values, conn)
                            return result
                        else:
                            return "No values to insert, remember to list them in the body"
                    else:
                        return "No table parameter"
                else:
                    return "No queryStringParameters"
            elif event['rawPath'] == updatelink:
                if 'queryStringParameters' in event:
                    if 'table' in event['queryStringParameters']:
                        if 'body' in event:
                            try:
                                values = event['body'].rsplit(",") 
                                logger.info(values)
                            except:
                                return "Values deliminated incorrectly, remember to commas between values with no spaces"
                            result = updateQuery(event['queryStringParameters']['table'], values, conn)
                            return result
                        else:
                            return "No values to update, remember to list them in the body"
                    else:
                        return "No table parameter"
                else:
                    return "No queryStringParameters"
            elif event['rawPath'] == deletelink:
                if 'queryStringParameters' in event:
                    if 'table' in event['queryStringParameters']:
                        if 'col' in event['queryStringParameters']:
                            result = deleteRow(event['queryStringParameters']['table'], event['queryStringParameters']['col'], event['body'], conn)
                            return result
                        else:
                            return "No column specified"
                    else:
                        return "No table specified"
                else:
                    return "No parameters"
            else: 
                return "Wrong URL"
        else:
            return "Wrong URL"
    else:
        return "No route"
        

def numRows(table, cursor):
    sqlString = "SELECT count(*) FROM " + table + ";"
    cursor.execute(sqlString)
    rows = cursor.fetchone()
    return rows
    
def queryAll(table, conn):
    if table in validTables:
        with conn.cursor() as cur:
            sqlString = 'Select * FROM ' + table + ";"
            cur.execute(sqlString)
            result = cur.fetchall()
            conn.commit()
            # Close connection
            cur.close()
            return result
    else:
        errorStr = "Query error: table " + table + " is invalid" 
        return errorStr
        
def queryCol(table, col, conn):
    if table in validTables:
        if col in validCols:
            with conn.cursor() as cur:
                sqlString = 'Select ' + col + ' FROM ' + table + ';'
                cur.execute(sqlString)
                result = cur.fetchall()
                conn.commit()
                # Close connection
                cur.close()
                return result
        else:
            errorStr = "Query error: column " + col + " is invalid" 
            return errorStr 
    else:
        errorStr = "Query error: table " + table + " is invalid" 
        return errorStr
        
def insertQuery(table, values, conn):
    with conn.cursor() as cur:
        if numRows(table, cur)[0] >= 50:
            return "Error: table is full"
        if table == "ids" and len(values) == 1:
            if validateValues(values):
                postgres_insert_query = """ INSERT INTO ids (Name) VALUES (%s)"""
                record_to_insert = (values[0],)
                cur.execute(postgres_insert_query, record_to_insert)
                conn.commit()
                cur.close()
                return "Success"
            else:
                return "Values not valid"
        elif table == "shoppinglist" and len(values) == 4:
            if validateValues(values):
                try:
                    quantity = int(values[1])
                    groupid = int(values[2])
                    if values[3] == 'true':
                        marked = True
                    else:
                        marked = False
                except:
                    return "Error, values have wrong type"
                postgres_insert_query = """ INSERT INTO shoppinglist (name, Quantity, GroupID, Marked) VALUES (%s, %s, %s, %s)"""
                record_to_insert = (values[0], quantity, groupid, marked)
                cur.execute(postgres_insert_query, record_to_insert)
                conn.commit()
                cur.close()
                return "Success"
            else:
                return "Values not valid"
        elif table == "inventory" and len(values) == 5:
            if validateValues(values):
                # try:
                #     quantity = int(values[1])
                # except:
                #     return "Error, values have wrong type"
                postgres_insert_query = """ INSERT INTO inventory (id, date, name, quantity, uom) VALUES (%s, %s, %s, %s, %s)"""
                record_to_insert = (values[0], values[1], values[2], values[3], values[4])
                cur.execute(postgres_insert_query, record_to_insert)
                conn.commit()
                cur.close()
                return "Success"
            else:
                return "Values not valid"
        elif table == "items" and len(values) == 2:
            if validateValues(values):
                postgres_insert_query = """ INSERT INTO items (alias, store) VALUES (%s, %s)"""
                record_to_insert = (values[0], values[1])
                cur.execute(postgres_insert_query, record_to_insert)
                conn.commit()
                cur.close()
                return "Success"
            else:
                return "Values not valid"
        elif table == "list_items" and len(values) == 5:
            if validateValues(values):
                try:
                    if values[3] == 'true':
                        marked = True
                    else:
                        marked = False
                except:
                    return "Error, values have wrong type"
                postgres_insert_query = """ INSERT INTO list_items (id, name, quantity, marked, groupid) VALUES (%s, %s, %s, %s, %s)"""
                record_to_insert = (values[0], values[1], values[2], values[3], values[4])
                cur.execute(postgres_insert_query, record_to_insert)
                conn.commit()
                cur.close()
                return "Success"
            else:
                return "Values not valid"
        elif table == "lists" and len(values) == 2:
            if validateValues(values):
                postgres_insert_query = """ INSERT INTO lists (id, name) VALUES (%s, %s)"""
                record_to_insert = (values[0], values[1])
                cur.execute(postgres_insert_query, record_to_insert)
                conn.commit()
                cur.close()
                return "Success"
            else:
                return "Values not valid"
        else:
            errorStr = "Query error: table " + table + " is invalid or request has too many values" 
            return errorStr
        

def validateValues(values):
    for value in values:
        if set(value) <= allowedChars:
            pass
        else:
            return False
    return True
    
def deleteRow(table, col, value, conn):
    if table in validTables:
        if col in validCols:
            if validateValues(value):
                with conn.cursor() as cur:
                    sqlString = 'DELETE FROM ' + table + ' WHERE ' + col + ' = ' + "\'" + value + "\'"
                    cur.execute(sqlString)
                    conn.commit()
                    # Close connection
                    cur.close()
                    return "Success"
            else:
                "Values are invalid"
        else:
            errorStr = "Column not found" 
            return errorStr 
    else:
        errorStr = "Query error: table " + table + " is invalid" 
        return errorStr
        
def updateQuery(table, values, conn):
    with conn.cursor() as cur:
        if table == "inventory" and len(values) == 4:
            if validateValues(values):
                # try:
                #     quantity = int(values[1])
                # except:
                #     return "Error, values have wrong type"
                postgres_insert_query = """  UPDATE inventory set name = %s, quantity = %s, uom = %s where id = %s"""
                record_to_insert = (values[0], values[1], values[2], values[3])
                cur.execute(postgres_insert_query, record_to_insert)
                conn.commit()
                cur.close()
                return "Success"
            else:
                return "Values not valid"
        else:
            errorStr = "Query error: table " + table + " is invalid or request has too many values" 
            return errorStr