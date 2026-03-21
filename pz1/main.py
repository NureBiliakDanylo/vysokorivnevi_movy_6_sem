import datetime

#1. Виведення від 1 до 10
def print_numbers_to_ten():
    print("Числа від 1 до 10:")
    for i in range(1, 11):
        print(i)
    print("\n")


#2. Серзнач трьох чисел
def calculate_average(a, b, c):
    return (a + b + c) / 3


#3. Обчислення віку
def calculate_age(birth_year):
    current_year = datetime.datetime.now().year
    return current_year - birth_year


#4. Клас Книга
class Book:
    
    def __init__(self, title, author, year):
        self.title = title
        self.author = author
        self.year = year

    def display_info(self):
        print(f"Книга: '{self.title}'")
        print(f"Автор: {self.author}")
        print(f"Рік видання: {self.year}")


def main():
    # Демонстрація 1
    print_numbers_to_ten()
    print("-" * 30)

    # Демонстрація 2
    print("Знаходження середнього значення")
    try:
        num1 = float(input("Введіть перше число: "))
        num2 = float(input("Введіть друге число: "))
        num3 = float(input("Введіть третє число: "))
        avg = calculate_average(num1, num2, num3)
        print(f"Середнє значення: {avg:.2f}")
    except ValueError:
        print("Помилка: Будь ласка, вводьте лише числа.")
    print("-" * 30)

    # Демонстрація 3
    print("Обчислення віку")
    try:
        birth_year = int(input("Введіть ваш рік народження (наприклад, 1990): "))
        age = calculate_age(birth_year)
        if age < 0 or age > 150:
            print("Введено некоректний рік народження.")
        else:
            print(f"Вам виповниться (або вже виповнилося) {age} років.")
    except ValueError:
        print("Помилка: Рік має бути цілим числом.")
    print("-" * 30)

    # Демонстрація 4
    print("Клас книга")
    my_book = Book("Тіні забутих предків", "Михайло Коцюбинський", 1911)
    my_book.display_info()

if __name__ == "__main__":
    main()