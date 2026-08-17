function loadBooks () {
    const api="/books";
    const tablebody=document.getElementById("Table-body-js");
    const tablehead=document.getElementById("Table-head-js")
    const button = document.getElementById("loadbooks");



    fetch(api)
        .then(response => response.json())
        .then(data => {

            const head=document.createElement("tr")
            head.innerHTML=`
            <th>Id</th>
            <th>Name</th>
            <th>Description</th>
            <th>Category</th>
            <th>Stock</th>
            <th>Availability</th>`;

            tablehead.appendChild(head);
            button.style.display = "none";

            data.forEach(book => {
                const row = document.createElement("tr")
                row.innerHTML= `
                <td> ${book.id} </td>
                <td> ${book.name} </td>
                <td> ${book.description} </td>
                <td> ${book.category} </td>
                <td> ${book.stock} </td>
                <td> ${book.availability} </td>
                `
                tablebody.appendChild(row);
            })
        }).catch(error => {
            console.log(error);
    })


}