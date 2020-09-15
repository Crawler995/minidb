import unittest

from minidb_run import run_commands_in_minidb


class BplussTreeTest(unittest.TestCase):
    def test_insert_and_get_1(self):
        test_commands = [
            'insert',
            '1',
            '11',
            'insert',
            '1',
            '11',
            'insert',
            '2',
            '22',
            'insert',
            '3',
            '33',
            'insert',
            '1000',
            '10001000',

            'get',
            '1',
            'get',
            '2',
            'get',
            '3',
            'get',
            '1000'
        ]
        expected_output = [
            '[11, 11]',
            '[22]',
            '[33]',
            '[10001000]'
        ]
        real_output = run_commands_in_minidb(test_commands)

        self.assertListEqual(real_output, expected_output)


if __name__ == "__main__":
    unittest.main()
