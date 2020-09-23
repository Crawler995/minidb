import unittest
import requests
import json

from minidb_run import resolve_arr, resolve_obj


class BplussTreeTest(unittest.TestCase):
    def test_insert_and_get_1(self):
        test_commands_arr = [
            'create database auto_test_db;',
            'use auto_test_db;',
            'create table auto_test_table_1 (id long, name varchar);',
            'insert into auto_test_table_1(id, name) values(1, "name1");',
            'insert into auto_test_table_1(id, name) values(2, "name2");',
            'insert into auto_test_table_1(id, name) values(3, "name3");',
            'select * from auto_test_table_1;'
        ]
        test_commands = '\n'.join(test_commands_arr)

        res = requests.post('http://localhost:8082/api/runsql', json={ 'command': test_commands }).json()
        self.assertListEqual(res[len(res) - 1]['columns'], resolve_arr(['id', 'name']))
        self.assertListEqual(res[len(res) - 1]['data'], resolve_obj([
            { 'id': 1, 'name': 'name1' },
            { 'id': 2, 'name': 'name2' },
            { 'id': 3, 'name': 'name3' },
        ]))


if __name__ == "__main__":
    unittest.main()
