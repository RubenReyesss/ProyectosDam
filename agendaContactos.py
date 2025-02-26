import sqlite3
import tkinter as tk
from tkinter import messagebox
from tkinter import ttk
import re

def crear_bd():
    conexion = sqlite3.connect("agenda.db")
    cursor = conexion.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS contacto (
                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                   nombre TEXT,
                   telefono TEXT,
                   correo TEXT)''')
    conexion.commit()
    conexion.close()

def agregar_contacto():
    nombre = entry_nombre.get()
    telefono = entry_telefono.get()
    correo = entry_correo.get()

    if not nombre.isalpha() and " " not in nombre:
        messagebox.showwarning("Error", "El nombre solo puede contener letras y espacios.")
        return

    if not telefono.isdigit() or len(telefono) != 9:
        messagebox.showwarning("Error", "El teléfono debe tener exactamente 9 números.")
        return

    correo_regex = r"^[a-zA-Z0-9._%+-]+@gmail\.com$"
    if not re.match(correo_regex, correo):
        messagebox.showwarning("Error", "El correo debe tener el formato 'usuario@gmail.com'.")
        return

    conexion = sqlite3.connect("agenda.db")
    cursor = conexion.cursor()
    cursor.execute("INSERT INTO contacto (nombre, telefono, correo) VALUES (?, ?, ?)", (nombre, telefono, correo))
    conexion.commit()
    conexion.close()

    messagebox.showinfo("¡Éxito!", "Contacto añadido con éxito")
    listar_contactos()

def listar_contactos():
    conexion = sqlite3.connect("agenda.db")
    cursor = conexion.cursor()
    cursor.execute("SELECT * FROM contacto")
    contactos = cursor.fetchall()
    conexion.close()

    listbox_contactos.delete(0, tk.END)
    for contacto in contactos:
        listbox_contactos.insert(tk.END, f"{contacto[0]} - {contacto[1]} - {contacto[2]} - {contacto[3]}")

def buscar_contacto():
    nombre = entry_nombre.get()
    conexion = sqlite3.connect("agenda.db")
    cursor = conexion.cursor()
    cursor.execute("SELECT * FROM contacto WHERE nombre LIKE ?", ("%"+nombre+"%",))
    contactos = cursor.fetchall()
    conexion.close()

    listbox_contactos.delete(0, tk.END)
    for contacto in contactos:
        listbox_contactos.insert(tk.END, f"{contacto[0]} - {contacto[1]} - {contacto[2]} - {contacto[3]}")

def eliminar_contacto():
    seleccion = listbox_contactos.curselection()
    if seleccion:
        contacto = listbox_contactos.get(seleccion)
        id_contacto = contacto.split(" - ")[0]
        conexion = sqlite3.connect("agenda.db")
        cursor = conexion.cursor()
        cursor.execute("DELETE FROM contacto WHERE id = ?", (id_contacto,))
        conexion.commit()
        conexion.close()
        messagebox.showinfo("Éxito", "Contacto eliminado")
        listar_contactos()
    else:
        messagebox.showwarning("Error", "Seleccione un contacto para eliminar")

def cerrar_app():
    root.destroy()

root = tk.Tk()
root.title("Agenda de Contactos")
root.geometry("600x550")
root.configure(bg="#FAF3E0")

style = ttk.Style()
style.configure("TButton", font=("Arial", 12, "bold"), padding=10, background="#FFD3B6", foreground="white", borderwidth=2)
style.map("TButton", foreground=[("active", "white")], background=[("active", "#FFA69E")])
style.configure("TLabel", font=("Arial", 12, "bold"), background="#FAF3E0", foreground="#444444")
style.configure("TEntry", font=("Arial", 12), padding=5)

frame = tk.Frame(root, bg="#FAF3E0")
frame.pack(pady=10)

ttk.Label(frame, text="Nombre: ").grid(row=0, column=0, padx=5, pady=5)
ttk.Label(frame, text="Teléfono: ").grid(row=1, column=0, padx=5, pady=5)
ttk.Label(frame, text="Correo: ").grid(row=2, column=0, padx=5, pady=5)

entry_nombre = ttk.Entry(frame, width=30)
entry_telefono = ttk.Entry(frame, width=30)
entry_correo = ttk.Entry(frame, width=30)

entry_nombre.grid(row=0, column=1, padx=5, pady=5)
entry_telefono.grid(row=1, column=1, padx=5, pady=5)
entry_correo.grid(row=2, column=1, padx=5, pady=5)

button_frame = tk.Frame(root, bg="#FAF3E0")
button_frame.pack(pady=10)

boton_agregar = tk.Button(button_frame, text="Agregar Contacto", command=agregar_contacto, bg="#FFB6B9", fg="white", font=("Arial", 12, "bold"), relief="flat", width=20)
boton_listar = tk.Button(button_frame, text="Listar Contactos", command=listar_contactos, bg="#FFDAC1", fg="white", font=("Arial", 12, "bold"), relief="flat", width=20)
boton_eliminar = tk.Button(button_frame, text="Eliminar Contacto", command=eliminar_contacto, bg="#FF9AA2", fg="white", font=("Arial", 12, "bold"), relief="flat", width=20)
boton_buscar = tk.Button(button_frame, text="Buscar Contacto", command=buscar_contacto, bg="#B5EAD7", fg="white", font=("Arial", 12, "bold"), relief="flat", width=20)
boton_cerrar = tk.Button(root, text="Cerrar Aplicación", command=cerrar_app, bg="#A2D5F2", fg="white", font=("Arial", 12, "bold"), relief="flat", width=20)

boton_agregar.grid(row=0, column=0, padx=5, pady=5)
boton_listar.grid(row=0, column=1, padx=5, pady=5)
boton_eliminar.grid(row=1, column=0, padx=5, pady=5)
boton_buscar.grid(row=1, column=1, padx=5, pady=5)

boton_cerrar.pack(pady=10)

listbox_contactos = tk.Listbox(root, width=60, height=10, bg="#FFEBEE", fg="#444444", font=("Arial", 12))
listbox_contactos.pack(pady=10)

crear_bd()
listar_contactos()

root.mainloop()
