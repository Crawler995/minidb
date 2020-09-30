import unittest
import requests
import json

from minidb_run import resolve_arr, resolve_obj


class DBAutoTest(unittest.TestCase):
    def test_insert_and_get_1(self):
        test_commands_arr = [
            'create database auto_test_db;',
            'use auto_test_db;',
            'create table auto_test_table_1 (birthday datetime, name varchar);',
            'insert into auto_test_table_1(birthday, name) values("2020-01-22 12:00:00", "name1");',
            'insert into auto_test_table_1(birthday, name) values("2020-04-23 13:00:00", "name2");',
            'insert into auto_test_table_1(birthday, name) values("2020-02-25 14:00:00", "name3");',
            'select * from auto_test_table_1;'
        ]
        test_commands = '\n'.join(test_commands_arr)

        res = requests.post('http://localhost:8082/api/runsql', json={ 'command': test_commands }).json()
        self.assertListEqual(res[len(res) - 1]['columns'], resolve_arr(['birthday', 'name']))
        self.assertListEqual(res[len(res) - 1]['data'], resolve_obj([
            { 'birthday': "2020-01-22 12:00:00", 'name': 'name1' },
            { 'birthday': "2020-04-23 13:00:00", 'name': 'name2' },
            { 'birthday': "2020-02-25 14:00:00", 'name': 'name3' },
        ]))


if __name__ == "__main__":
    unittest.main()
